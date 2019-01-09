package pl.north93.northplatform.antycheat.timeline.impl;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import lombok.ToString;
import pl.north93.northplatform.antycheat.timeline.PlayerTickInfo;
import pl.north93.northplatform.antycheat.timeline.Tick;

@ToString
/*default*/ class PlayerTickInfoImpl implements PlayerTickInfo
{
    private final Player  player;
    private final Tick    tick;
    private final boolean afterSpawn;
    private final boolean afterTeleport;
    private final boolean reliable;
    private final int     ping;
    private final boolean gliding;
    private final double  movementSpeed;

    public PlayerTickInfoImpl(final Player player, final Tick tick, final boolean afterSpawn, final boolean afterTeleport, final boolean reliable)
    {
        this.player = player;
        this.tick = tick;
        this.afterSpawn = afterSpawn;
        this.afterTeleport = afterTeleport;
        this.reliable = reliable;

        this.ping = player.spigot().getPing();
        this.gliding = player.isGliding();
        this.movementSpeed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue();
    }

    @Override
    public Player getOwner()
    {
        return this.player;
    }

    @Override
    public Tick getTick()
    {
        return this.tick;
    }

    @Override
    public int getPing()
    {
        return this.ping;
    }

    @Override
    public boolean isShortAfterSpawn()
    {
        return this.afterSpawn;
    }

    @Override
    public boolean isShortAfterTeleport()
    {
        return this.afterTeleport;
    }

    @Override
    public boolean hasReceivedPacket()
    {
        return this.reliable;
    }

    @Override
    public boolean isGliding()
    {
        return this.gliding;
    }

    @Override
    public double getMovementSpeed()
    {
        return this.movementSpeed;
    }
}
