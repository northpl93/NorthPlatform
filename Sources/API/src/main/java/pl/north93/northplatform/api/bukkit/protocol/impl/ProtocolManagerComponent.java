package pl.north93.northplatform.api.bukkit.protocol.impl;

import static pl.north93.northplatform.api.bukkit.utils.nms.EntityTrackerHelper.toNmsPlayer;


import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Collection;

import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PlayerConnection;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.spigotmc.SpigotConfig;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.protocol.ChannelWrapper;
import pl.north93.northplatform.api.bukkit.protocol.PacketHandler;
import pl.north93.northplatform.api.bukkit.protocol.ProtocolManager;
import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Aggregator;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.component.annotations.bean.Named;
import pl.north93.northspigot.event.ChannelInitializeEvent;

@Slf4j
public class ProtocolManagerComponent extends Component implements ProtocolManager, Listener
{
    private final PacketEventDispatcher asyncDispatcher = new PacketEventDispatcher();
    //private final PacketEventDispatcher syncDispatcher = new PacketEventDispatcher();
    
    @Inject
    private IBukkitServerManager serverManager;
    
    @Override
    protected void enableComponent()
    {
        closeListenerIfAlreadyOpened();
        
        serverManager.registerEvents(this);
    }

    @Override
    protected void disableComponent()
    {
        // XXX: shall we clean up that mess in pipeline we made?
    }

    @Aggregator(PacketHandler.class)
    public void aggregatePacketHandlers(PacketHandler packetHandlerAnnotation, @Named("Target") Method method, @Named("MethodOwner") Object instance)
    {
        log.debug("Called aggregator method for {} on method {} object {}", packetHandlerAnnotation, method.getName(), instance);
        
        if ( packetHandlerAnnotation.sync() )
        {
            // TODO: sync handler calling
            //syncDispatcher.addMethod(method, instance, packetHandlerAnnotation.priority());
        }
        else
        {
            asyncDispatcher.addMethod(method, instance, packetHandlerAnnotation.priority());
        }
    }
    
    @Override
    public Channel getChannel(Player player)
    {
        return toNmsPlayer(player).playerConnection.networkManager.channel;
    }

    @Override
    public ChannelWrapper getChannelWrapper(Channel channel)
    {
        NorthChannelHandler channelHandler = channel.pipeline().get(NorthChannelHandler.class);
        return channelHandler != null ? channelHandler.getChannelWrapper() : null;
    }

    @Override
    public ChannelWrapper getChannelWrapper(Player player)
    {
        PlayerConnection playerConnection = toNmsPlayer(player).playerConnection;
        return playerConnection != null ? getChannelWrapper(playerConnection.networkManager.channel) : null;
    }
    
    @Override
    public ChannelFuture startNewListener(InetAddress address, int port)
    {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Collection<ChannelFuture> getActiveListeners()
    {
        throw new RuntimeException("Not implemented yet");
    }
    
    PacketEventDispatcher getAsyncDispatcher()
    {
        return asyncDispatcher;
    }
    
    PacketEventDispatcher getSyncDispatcher()
    {
        //return syncDispatcher;
        return null;
    }
    
    @EventHandler
    public void onInitChannel(ChannelInitializeEvent event)
    {
        Channel channel = event.getChannel();
        channel.pipeline().addBefore("packet_handler", "north_packet_handler", new NorthChannelHandler());

        log.debug("Injected own channel initializer for: " + channel);
    }
    
    @SuppressWarnings("deprecation")
    private void closeListenerIfAlreadyOpened()
    {   
        // we must close channels that was opened before we apply the patches to channel initializer
        // if we don't do it there might be players that are unhandled by our packet handler
        
        // if lateBing is false there are already listening channels
        if ( !SpigotConfig.lateBind )
        {
            log.debug("SpigotConfig.lateBind is false - closing all listeners");
            MinecraftServer.getServer().an().b(); // should be - getServerConnection().closeAllListeners()
            
            // we set lateBing to true to server initialize listeners again after enabling all plugins
            SpigotConfig.lateBind = true;
        }
    }
}
