package pl.north93.zgame.antycheat.timeline;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import pl.north93.zgame.antycheat.utils.location.RichEntityLocation;

public final class PlayerProperties
{
    private final RichEntityLocation location;
    private final boolean            sprinting;
    private final boolean            flying;
    private final boolean            gliding;
    private final double             movementSpeed;
    private final int                ping;
    private final Vector             velocity;

    public PlayerProperties(final Player player)
    {
        this.location = new RichEntityLocation(player, player.getLocation());
        this.sprinting = player.isSprinting();
        this.flying = player.isFlying();
        this.gliding = player.isGliding();
        this.movementSpeed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue();
        this.ping = player.spigot().getPing();
        this.velocity = player.getVelocity();
    }

    public RichEntityLocation getLocation()
    {
        return this.location;
    }

    public boolean isSprinting()
    {
        return this.sprinting;
    }

    public boolean isFlying()
    {
        return this.flying;
    }

    public boolean isGliding()
    {
        return this.gliding;
    }

    public double getMovementSpeed()
    {
        return this.movementSpeed;
    }

    public int getPing()
    {
        return this.ping;
    }

    public Vector getVelocity()
    {
        return this.velocity;
    }
}
