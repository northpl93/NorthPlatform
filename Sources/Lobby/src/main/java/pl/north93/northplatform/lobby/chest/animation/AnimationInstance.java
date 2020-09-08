package pl.north93.northplatform.lobby.chest.animation;

import org.bukkit.entity.ArmorStand;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.hologui.IIcon;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

@Slf4j
public final class AnimationInstance
{
    private final INorthPlayer owner;
    private final IIcon icon;
    private ChestAnimation currentAnimation;
    private boolean destroyed;

    AnimationInstance(final INorthPlayer owner, final IIcon icon)
    {
        this.owner = owner;
        this.icon = icon;
    }

    public void setAnimation(final ChestAnimation chestAnimation)
    {
        this.currentAnimation = chestAnimation;
    }

    public INorthPlayer getOwner()
    {
        return this.owner;
    }

    public IIcon getIcon()
    {
        return this.icon;
    }

    public ArmorStand getArmorStand()
    {
        return this.icon.getBackingArmorStand();
    }

    public void tick()
    {
        try
        {
            this.currentAnimation.tick();
        }
        catch (final Exception e)
        {
            log.error("Exception thrown in chest AnimationInstance", e);
        }
    }

    public void setDestroyed()
    {
        this.destroyed = true;
    }

    public boolean isDestroyed()
    {
        return this.destroyed;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("owner", this.owner).append("icon", this.icon).append("currentAnimation", this.currentAnimation).append("destroyed", this.destroyed).toString();
    }
}
