package pl.north93.zgame.skyblock.shop.cmd;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.skyblock.shop.ShopComponent;

public class MoneySetCmd extends NorthCommand
{
    @Inject
    private ShopComponent shopComponent;

    public MoneySetCmd()
    {
        super("moneyset");
        this.setPermission("money.set");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() != 2)
        {
            sender.sendMessage("&f&l> &6/moneyset <nick> <stan>");
            return;
        }

        final String nick = args.asString(0);
        final Double value = args.asDouble(1);

        this.shopComponent.getShopManager().setMoney(nick, value);
        sender.sendMessage("&f&l> &7Ustawiono stan konta &6" + nick + " &7na &6" + value);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("shopComponent", this.shopComponent).toString();
    }
}
