package pl.north93.zgame.skyblock.shop.cmd;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.skyblock.shop.ShopComponent;

public class PayCmd extends NorthCommand
{
    @Inject
    private ShopComponent shopComponent;
    @Inject
    private INetworkManager networkManager;

    public PayCmd()
    {
        super("pay", "zaplac");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() != 2)
        {
            sender.sendRawMessage("&f&l> &7Uzyj &6/pay nick ilosc");
            return;
        }

        final String nick = args.asString(0);
        final Double value = args.asDouble(1);
        if (value == null)
        {
            sender.sendRawMessage("&f&l> &7Niepoprawne argumenty komendy.");
            return;
        }

        final Player payer = (Player) sender.unwrapped();
        if ((value <= 1 || value.toString().split("\\.")[1].length() > 3) && !payer.hasPermission("money.set"))
        {
            sender.sendRawMessage("&f&l> &7Minimalna kwota przelewu to &61&7!");
            return;
        }

        if (sender.getName().equalsIgnoreCase(nick))
        {
            sender.sendRawMessage("&f&l> &7Nie mozesz wyslac pieniedzy do siebie :(");
            return;
        }

        final boolean success = this.shopComponent.getShopManager().pay(payer.getUniqueId(), nick, value);
        if (success)
        {
            sender.sendRawMessage("&f&l> &7Pomyslnie przelano &6" + value + " &7do &6" + nick + "&7!");

            this.networkManager.getPlayers().ifOnline(nick, iOnlinePlayer ->
            {
                iOnlinePlayer.sendRawMessage("&f&l> &7Otrzymales wlasnie &6" + value + " &7od &6" + sender.getName() + "&7!");
            });
        }
        else
        {
            sender.sendRawMessage("&f&l> &7Nie udalo sie przelac waluty.");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}