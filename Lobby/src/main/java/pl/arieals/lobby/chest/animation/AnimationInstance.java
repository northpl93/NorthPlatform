package pl.arieals.lobby.chest.animation;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

class AnimationInstance
{
    private final Player     owner;
    private final Location   location;
    private final ArmorStand armorStand;
    private ChestAnimation   currentAnimation;
    private boolean destroyed;

    AnimationInstance(final Player owner, final Location location, final ArmorStand armorStand)
    {
        this.owner = owner;
        this.location = location;
        this.armorStand = armorStand;
    }

    public void setAnimation(final ChestAnimation chestAnimation)
    {
        this.currentAnimation = chestAnimation;
    }

    public Player getOwner()
    {
        return this.owner;
    }

    public ArmorStand getArmorStand()
    {
        return this.armorStand;
    }

    public void tick()
    {
        try
        {
            this.currentAnimation.tick();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setDestroyed()
    {
        this.destroyed = true;

        // zabijamy armor stand bez efektu
        ((CraftArmorStand) this.armorStand).getHandle().die();
    }

    public boolean isDestroyed()
    {
        return this.destroyed;
    }

    public void handleClick()
    {
        this.currentAnimation.clicked();
    }
}
