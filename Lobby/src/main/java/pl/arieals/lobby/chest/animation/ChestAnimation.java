package pl.arieals.lobby.chest.animation;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

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

    abstract void clicked();
}
