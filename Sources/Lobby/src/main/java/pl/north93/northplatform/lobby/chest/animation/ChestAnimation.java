package pl.north93.northplatform.lobby.chest.animation;

import org.bukkit.entity.ArmorStand;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

abstract class ChestAnimation
{
    protected final AnimationInstance instance;

    public ChestAnimation(final AnimationInstance instance)
    {
        this.instance = instance;
    }

    protected final INorthPlayer getPlayer()
    {
        return this.instance.getOwner(); // player who sees this animation
    }

    protected final ArmorStand getArmorStand()
    {
        return this.instance.getArmorStand();
    }

    abstract void tick();

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
