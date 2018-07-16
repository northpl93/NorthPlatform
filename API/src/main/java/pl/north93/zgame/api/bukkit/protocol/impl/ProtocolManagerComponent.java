package pl.north93.zgame.api.bukkit.protocol.impl;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

import com.google.common.base.Preconditions;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.NetworkManager;

import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.protocol.ChannelWrapper;
import pl.north93.zgame.api.bukkit.protocol.PacketHandler;
import pl.north93.zgame.api.bukkit.protocol.ProtocolManager;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.component.annotations.bean.Named;

public class ProtocolManagerComponent extends Component implements ProtocolManager
{
    private static final Logger logger = LogManager.getLogger();
    
    @Override
    protected void enableComponent()
    {
        closeListenerIfAlreadyOpened();
        applyPatches();
    }

    @Override
    protected void disableComponent()
    {
        // XXX: shall we clean up that shit we made?
    }

    @Aggregator(PacketHandler.class)
    public void aggregatePacketHandlers(PacketHandler packetHandlerAnnotation, @Named("Target") Method method, @Named("MethodOwner") Object instance)
    {
        logger.debug("Called aggregator method for {} on method {} object {}", packetHandlerAnnotation, method.getName(), instance);
        // TODO: implement this
    }
    
    @Override
    public Channel getChannel(Player player)
    {
        return INorthPlayer.asCraftPlayer(player).getHandle().playerConnection.networkManager.channel;
    }

    @Override
    public ChannelWrapper getChannelWrapper(Channel channel)
    {
        // TODO:
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public ChannelWrapper getChannelWrapper(Player player)
    {
        // TODO:
        throw new RuntimeException("Not implemented yet");
    }
    
    @Override
    public ChannelFuture startNewListener(InetAddress address, int port)
    {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Collection<ChannelFuture> getActiveListeners()
    {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet");
    }
 
    void initChannel(Channel channel)
    {
        NetworkManager networkManager = channel.pipeline().get(NetworkManager.class);
        Preconditions.checkState(networkManager != null);
        
        logger.debug("Injected own channel initializer for: " + channel);
    }
    
    @SuppressWarnings("deprecation")
    private void closeListenerIfAlreadyOpened()
    {
        // we must close channels that was opened before we apply the patches to channel initializer
        // if we don't do it there might be players that are unhandled by our packet handler
        
        // if lateBing is false there are already listening channels
        if ( !SpigotConfig.lateBind )
        {
            logger.debug("SpigotConfig.lateBind is false - closing all listeners");
            MinecraftServer.getServer().an().b(); // should be - getServerConnection().closeAllListeners()
            
            // we set lateBing to true to server initialize listeners again after enabling all plugins
            SpigotConfig.lateBind = true;
        }
    }
    
    private void applyPatches()
    {
        try
        {
            ServerConnectionPatcher.patch();
        }
        catch ( Throwable e )
        {
            logger.error("Couldn't apply patches for ProtocolManagerComponent:", e);
        }
    }
}
