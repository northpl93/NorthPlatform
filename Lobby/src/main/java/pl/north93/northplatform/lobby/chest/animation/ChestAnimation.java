package pl.north93.northplatform.lobby.chest.animation;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

abstract class ChestAnimation
{
    protected final AnimationInstance instance;

    public ChestAnimation(final AnimationInstance instance)
    {
        this.instance = instance;
    }

    protected final Player getPlayer()
    {
        return this.instance.getOwner(); // gracz ktory widzi ta animacje
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
