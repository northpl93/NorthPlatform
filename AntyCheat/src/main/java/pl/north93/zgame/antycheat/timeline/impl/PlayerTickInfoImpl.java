package pl.north93.zgame.antycheat.timeline.impl;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.antycheat.timeline.PlayerProperties;
import pl.north93.zgame.antycheat.timeline.PlayerTickInfo;
import pl.north93.zgame.antycheat.timeline.Tick;

/*default*/ class PlayerTickInfoImpl implements PlayerTickInfo
{
    private final Player           player;
    private final Tick             tick;
    private final PlayerProperties properties;
    private final boolean          afterSpawn;
    private final boolean          afterTeleport;
    private final boolean          reliable;

    public PlayerTickInfoImpl(final Player player, final Tick tick, final PlayerProperties properties, final boolean afterSpawn, final boolean afterTeleport, final boolean reliable)
    {
        this.player = player;
        this.tick = tick;
        this.properties = properties;
        this.afterSpawn = afterSpawn;
        this.afterTeleport = afterTeleport;
        this.reliable = reliable;
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
    public PlayerProperties getProperties()
    {
        return this.properties;
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
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).append("tick", this.tick).append("reliable", this.reliable).toString();
    }
}
