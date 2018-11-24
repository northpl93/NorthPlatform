package pl.north93.northplatform.api.economy.impl.shared.cmd;

import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.economy.IAccountAccessor;
import pl.north93.northplatform.api.economy.ICurrency;
import pl.north93.northplatform.api.economy.IEconomyManager;
import pl.north93.northplatform.api.economy.ITransaction;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.players.Identity;

@Slf4j
public class EconomyAdminCommand extends NorthCommand
{
    @Inject
    private IEconomyManager economyManager;

    public EconomyAdminCommand()
    {
        super("economyadmin", "ecoadm");
        this.setPermission("economy.cmd.economyadmin");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.isEmpty())
        {
            this.showHelp(sender, label);
            return;
        }

        switch (args.asString(0))
        {
            case "get":
                this.get(sender, args, label);
                break;
            case "set":
                this.set(sender, args, label);
                break;
            case "add":
                this.add(sender, args, label);
                break;
            default:
                this.showHelp(sender, label);
        }
    }

    private void showHelp(final NorthCommandSender sender, final String label)
    {
        final Collection<ICurrency> currencies = this.economyManager.getCurrencies();
        final String list = currencies.stream().map(ICurrency::getName).collect(Collectors.joining(", "));

        sender.sendMessage("&cDostepne waluty: {0}", list);
        sender.sendMessage("&c/{0} get <player> <waluta>", label);
        sender.sendMessage("&c/{0} set <player> <waluta> <ilosc>", label);
        sender.sendMessage("&c/{0} add <player> <waluta> <ilosc>", label);
    }

    private void get(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() < 3)
        {
            this.showHelp(sender, label);
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

    private void set(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() < 4)
        {
            this.showHelp(sender, label);
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
            log.error("Exception while setting currency amount", e);
        }
    }

    private void add(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() < 4)
        {
            this.showHelp(sender, label);
            return;
        }

        final String who = args.asString(1);
        final String currencyName = args.asString(2);
        final Integer amount = args.asInt(3);

        final Identity identity = Identity.create(null, who);
        final ICurrency currency = this.economyManager.getCurrency(currencyName);

        try (final ITransaction t = this.economyManager.openTransaction(currency, identity))
        {
            t.add(amount);

            final String nick = t.getAssociatedPlayer().getLatestNick();
            sender.sendMessage("&aStan konta {0} dla waluty {1} ustawiony na {2}", nick, currency.getName(), t.getAmount());
        }
        catch (final Exception e)
        {
            sender.sendMessage("&cWystapil blad podczas wykonywania");
            log.error("Exception while adding currency amount", e);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
