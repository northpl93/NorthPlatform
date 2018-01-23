package pl.north93.zgame.antycheat.timeline;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class PlayerProperties
{
    private final boolean isSprinting;
    private final boolean isFlying;
    private final int     ping;
    private final Vector  velocity;

    public PlayerProperties(final Player player)
    {
        this.isSprinting = player.isSprinting();
        this.isFlying = player.isFlying();
        this.ping = player.spigot().getPing();
        this.velocity = player.getVelocity();
    }

    public boolean isSprinting()
    {
        return this.isSprinting;
    }

    public boolean isFlying()
    {
        return this.isFlying;
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
