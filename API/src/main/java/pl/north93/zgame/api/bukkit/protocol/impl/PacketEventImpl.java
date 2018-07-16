package pl.north93.zgame.api.bukkit.protocol.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import net.minecraft.server.v1_12_R1.Packet;

import pl.north93.zgame.api.bukkit.protocol.ChannelWrapper;
import pl.north93.zgame.api.bukkit.protocol.PacketEvent;

public class PacketEventImpl <P extends Packet<?>> implements PacketEvent<P>
{
    private final AtomicInteger intents = new AtomicInteger(0);
    private final AtomicBoolean called = new AtomicBoolean(false);
    
    private final ChannelWrapper channelWrapper;
    private final Consumer<PacketEventImpl<P>> callback;
    
    private volatile P packet;
    private volatile boolean cancelled;
    
    PacketEventImpl(ChannelWrapper channelWrapper, P packet, Consumer<PacketEventImpl<P>> callback)
    {
        this.channelWrapper = channelWrapper;
        this.packet = packet;
        this.callback = callback;
    }
    
    @Override
    public P getPacket()
    {
        return packet;
    }

    @Override
    public void setPacket(P newPacket)
    {
        Preconditions.checkArgument(newPacket != null, "cannot set new packet to null");
        packet = newPacket;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean flag)
    {
        cancelled = flag;
    }

    @Override
    public ChannelWrapper getChannel()
    {
        return channelWrapper;
    }

    @Override
    public Player getPlayer()
    {
        return channelWrapper.getPlayer();
    }

    synchronized void postCall()
    {
        Preconditions.checkState(!called.getAndSet(true));
        
        if ( intents.get() == 0 )
        {
            callback.accept(this);
        }
    }
    
    @Override
    public synchronized Intent registerIntent()
    {
        Preconditions.checkState(!called.get(), "Intent cannot be register after event call");
        intents.incrementAndGet();
        return new IntentImpl();
    }
    
    private class IntentImpl implements Intent
    {
        private final AtomicBoolean completed = new AtomicBoolean(false);
        
        @Override
        public void complete()
        {
            Preconditions.checkState(!completed.getAndSet(true), "Intent cannot be completed twice");
            
            if ( intents.decrementAndGet() == 0 )
            {
                callback.accept(PacketEventImpl.this);
            }
        }
    }
}
