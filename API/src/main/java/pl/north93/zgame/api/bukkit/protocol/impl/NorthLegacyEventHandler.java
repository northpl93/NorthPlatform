package pl.north93.zgame.api.bukkit.protocol.impl;

import org.bukkit.Bukkit;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import net.minecraft.server.v1_12_R1.Packet;

import pl.north93.zgame.api.bukkit.packets.event.AsyncPacketInEvent;
import pl.north93.zgame.api.bukkit.packets.event.AsyncPacketOutEvent;

/**
 * That class provide backward compatibility to components that use old AsyncPacketInEvent and AsyncPacketOutEvent
 */
@SuppressWarnings("deprecation")
public class NorthLegacyEventHandler extends ChannelDuplexHandler
{
    private NorthChannelHandler channelHandler;
    
    NorthLegacyEventHandler()
    {
    }
    
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception
    {
        channelHandler = ctx.channel().pipeline().get(NorthChannelHandler.class);
        super.handlerAdded(ctx);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        channelHandler = null;
        super.channelInactive(ctx);
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        if ( !( msg instanceof Packet ) )
        {
            super.channelRead(ctx, msg);
            return;
        }
        
        AsyncPacketInEvent event = new AsyncPacketInEvent(channelHandler.getChannelWrapper().getPlayer(), (Packet<?>) msg);
        Bukkit.getPluginManager().callEvent(event);
        
        if ( !event.isCancelled() && event.getPacket() != null )
        {
            super.channelRead(ctx, event.getPacket());
        }
    }
    
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception
    {
        if ( !( msg instanceof Packet ) )
        {
            super.write(ctx, msg, promise);
            return;
        }
        
        AsyncPacketOutEvent event = new AsyncPacketOutEvent(channelHandler.getChannelWrapper().getPlayer(), (Packet<?>) msg);
        Bukkit.getPluginManager().callEvent(event);
        
        if ( !event.isCancelled() && event.getPacket() != null )
        {
            super.write(ctx, event.getPacket(), promise); 
        }
    }
}
