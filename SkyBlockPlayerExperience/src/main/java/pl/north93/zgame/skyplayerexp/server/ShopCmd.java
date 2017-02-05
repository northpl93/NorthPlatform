package pl.north93.zgame.skyplayerexp.server;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;

public class ShopCmd extends NorthCommand
{
    @InjectComponent("SkyBlock.PlayerExperience.Server")
    private ExperienceServer experience;

    public ShopCmd()
    {
        super("shop", "sklep");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        this.experience.getServerGuiManager().openShopCategories((Player) sender.unwrapped());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("experience", this.experience).toString();
    }
}
