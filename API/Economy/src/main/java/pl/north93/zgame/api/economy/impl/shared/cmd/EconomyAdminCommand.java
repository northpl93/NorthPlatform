package pl.north93.zgame.api.economy.impl.shared.cmd;

import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.economy.IAccountAccessor;
import pl.north93.zgame.api.economy.ICurrency;
import pl.north93.zgame.api.economy.IEconomyManager;
import pl.north93.zgame.api.economy.ITransaction;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.Identity;

public class EconomyAdminCommand extends NorthCommand
{
    @Inject
    private IEconomyManager economyManager;

    public EconomyAdminCommand()
    {
        super("economyadmin", "ecoadm");
        this.setPermission("dev");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.isEmpty())
        {
            this.showHelp(sender);
            return;
        }

        switch (args.asString(0))
        {
            case "get":
                this.get(sender, args);
                break;
            case "set":
                this.set(sender, args);
                break;
            default:
                this.showHelp(sender);
        }
    }

    private void showHelp(final NorthCommandSender sender)
    {
        final Collection<ICurrency> currencies = this.economyManager.getCurrencies();
        final String list = currencies.stream().map(ICurrency::getName).collect(Collectors.joining(", "));

        sender.sendMessage("&cDostepne waluty: {0}", list);
        sender.sendMessage("&c/economyadmin get <player> <waluta>");
        sender.sendMessage("&c/economyadmin set <player> <waluta> <ilosc>");
    }

    private void get(final NorthCommandSender sender, final Arguments args)
    {
        if (args.length() < 3)
        {
            this.showHelp(sender);
            return;
        }

        final String who = args.asString(1);
        final String currencyName = args.asString(2);

        final Identity identity = Identity.create(null, who);
        final ICurrency currency = this.economyManager.getCurrency(currencyName);

        final IAccountAccessor accessor = this.economyManager.getUnsafeAccessor(currency, identity);

        final String nick = accessor.getAssociatedPlayer().getLatestNick();
        sender.sendMessage("&aStan konta {0} dla waluty {1} to {2}", nick, currency.getName(), accessor.getAmount());
    }

    private void set(final NorthCommandSender sender, final Arguments args)
    {
        if (args.length() < 4)
        {
            this.showHelp(sender);
            return;
        }

        final String who = args.asString(1);
        final String currencyName = args.asString(2);
        final Integer amount = args.asInt(3);

        final Identity identity = Identity.create(null, who);
        final ICurrency currency = this.economyManager.getCurrency(currencyName);

        try (final ITransaction t = this.economyManager.openTransaction(currency, identity))
        {
            t.setAmount(amount);

            final String nick = t.getAssociatedPlayer().getLatestNick();
            sender.sendMessage("&aStan konta {0} dla waluty {1} ustawiony na {2}", nick, currency.getName(), amount);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
