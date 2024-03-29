package pl.north93.northplatform.api.bukkit.protocol;

import javax.annotation.Nullable;

import java.net.InetAddress;
import java.util.Collection;

import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public interface ProtocolManager
{
    /**
     * @return a channel for particular channel.
     */
    @Nullable
    Channel getChannel(Player player);
    
    /**
     * @return a channel wrapper for particular channel.
     */
    @Nullable
    ChannelWrapper getChannelWrapper(Channel channel);
    
    /**
     * @return a channel wrapper for particular player.
     */
    @Nullable
    ChannelWrapper getChannelWrapper(Player player);
    
    /**
     * Open a new listener socket with specified address and port.
     * 
     * @return a channel future associated with that socket.
     */
    ChannelFuture startNewListener(InetAddress address, int port);
    
    /**
     * @return collection of all active listeners.
     */
    Collection<ChannelFuture> getActiveListeners();
}
