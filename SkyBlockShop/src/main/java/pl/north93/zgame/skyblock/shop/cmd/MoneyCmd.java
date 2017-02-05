package pl.north93.zgame.skyblock.shop.cmd;

import java.text.DecimalFormat;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.skyblock.shop.ShopComponent;

public class MoneyCmd extends NorthCommand
{
    private final DecimalFormat format = new DecimalFormat("#.##");
    @InjectComponent("SkyBlock.Shop.Server")
    private ShopComponent shopComponent;

    public MoneyCmd()
    {
        super("money", "pieniadze");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final double balance = this.shopComponent.getShopManager().getBalance((Player) sender.unwrapped());
        sender.sendMessage("&f&l> &7Aktualny stan konta: &6" + this.format.format(balance));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("shopComponent", this.shopComponent).toString();
    }
}
