package pl.north93.zgame.api.bukkit.protocol.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;

import net.minecraft.server.v1_12_R1.Packet;

import pl.north93.zgame.api.bukkit.protocol.ChannelWrapper;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class NorthChannelHandler extends ChannelDuplexHandler
{
    private static final Logger logger = LogManager.getLogger();
    
    @Inject
    private static ProtocolManagerComponent protocolManager;
    
    private ChannelWrapper channelWrapper;
    
    public ChannelWrapper getChannelWrapper()
    {
        return channelWrapper;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        logger.debug("Channel active for {}", ctx.channel());
        channelWrapper = new ChannelWrapperImpl(ctx.channel());
        
        super.channelActive(ctx);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        logger.debug("Channel inactive for {}", ctx.channel());
        channelWrapper = null;
        
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        logger.debug("Channel {} READ {}, thread {}", () -> ctx.channel(), () -> msg.getClass().getSimpleName(), () -> Thread.currentThread().getName());
        
        if ( !( msg instanceof Packet ) )
        {
            super.channelRead(ctx, msg);
            return;
        }
        
        Packet<?> packet = (Packet<?>) msg;
        
        PacketEventImpl<? extends Packet<?>> event = new PacketEventImpl<>(channelWrapper, packet, e -> runInChannelEventLoop(() -> channelReadCallback(e)));
        protocolManager.getAsyncDispatcher().dispatch(packet.getClass(), event);
        event.postCall();
    }
    
    private void channelReadCallback(PacketEventImpl<?> event)
    {
        if ( !event.isCancelled() )
        {
            event.getChannel().getChannel().pipeline().context(this).fireChannelRead(event.getPacket());
        }
    }
    
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception
    {
        logger.debug("Channel {} WRITE {}, thread {}", () -> ctx.channel(), () -> msg.getClass().getSimpleName(), () -> Thread.currentThread().getName());

        if ( !( msg instanceof Packet ) )
        {
            super.write(ctx, msg, promise);
            return;
        }
        
        Packet<?> packet = (Packet<?>) msg;
        
        PacketEventImpl<? extends Packet<?>> event = new PacketEventImpl<>(channelWrapper, packet, e -> runInChannelEventLoop(() -> channelWriteCallback(e, promise)));
        protocolManager.getAsyncDispatcher().dispatch(packet.getClass(), event);
        event.postCall();
    }
    
    private void channelWriteCallback(PacketEventImpl<?> event, ChannelPromise promise)
    {
        if ( !event.isCancelled() )
        {
            event.getChannel().getChannel().pipeline().context(this).write(event.getPacket(), promise);
        }
    }
    
    private void runInChannelEventLoop(Runnable task)
    {
        EventLoop eventLoop = channelWrapper.getChannel().eventLoop();
        
        if ( eventLoop.inEventLoop() )
        {
            task.run();
        }
        else
        {
            eventLoop.execute(task);
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        logger.debug("Exception caugh when processing channel pipeline:", cause);
        super.exceptionCaught(ctx, cause);
    }
}
