package pl.north93.zgame.antycheat.timeline.impl;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.antycheat.timeline.PlayerTickInfo;
import pl.north93.zgame.antycheat.timeline.Tick;

/*default*/ class PlayerTickInfoImpl implements PlayerTickInfo
{
    private final Player  player;
    private final Tick    tick;
    private final boolean afterSpawn;
    private final boolean reliable;
    private final int     ping;

    public PlayerTickInfoImpl(final Player player, final Tick tick, final boolean afterSpawn, final boolean reliable, final int ping)
    {
        this.player = player;
        this.tick = tick;
        this.afterSpawn = afterSpawn;
        this.reliable = reliable;
        this.ping = ping;
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
    public boolean isShortAfterSpawn()
    {
        return this.afterSpawn;
    }

    @Override
    public boolean hasReceivedPacket()
    {
        return this.reliable;
    }

    @Override
    public int getPing()
    {
        return this.ping;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).append("tick", this.tick).append("reliable", this.reliable).append("ping", this.ping).toString();
    }
}
