package pl.north93.zgame.skyblock.shop.cmd;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.IgnoreExtensionPoint;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.skyblock.shop.ShopComponent;

@IgnoreExtensionPoint // we have custom category picker in SkyBlock.PlayerExperience
public class ShopCmd extends NorthCommand
{
    @Inject
    private ShopComponent shopComponent;

    public ShopCmd()
    {
        super("shop", "sklep");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        this.shopComponent.getShopManager().openCategoriesPicker((Player) sender.unwrapped());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
