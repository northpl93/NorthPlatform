package pl.north93.zgame.api.bukkit.packets.event;

import javax.annotation.Nullable;

import net.minecraft.server.v1_12_R1.Packet;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Deprecated
public final class AsyncPacketOutEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private final Player  player;
    private       Packet  packet;
    private       boolean cancelled;

    public AsyncPacketOutEvent(final Player player, final Packet packet)
    {
        super(true); // async event
        this.player = player;
        this.packet = packet;
    }

    @Nullable
    public Player getPlayer()
    {
        return this.player;
    }

    public Packet getPacket()
    {
        return this.packet;
    }

    public void setPacket(final Packet packet)
    {
        this.packet = packet;
    }

    public boolean isType(final Class<? extends Packet> packetType)
    {
        return this.packet.getClass() == packetType;
    }

    @Override
    public boolean isCancelled()
    {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).append("packet", this.packet).append("cancelled", this.cancelled).toString();
    }
}
