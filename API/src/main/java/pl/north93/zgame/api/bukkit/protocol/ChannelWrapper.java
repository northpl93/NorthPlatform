package pl.north93.zgame.api.bukkit.protocol;

import javax.annotation.Nullable;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.NetworkManager;
import net.minecraft.server.v1_12_R1.Packet;

import org.bukkit.entity.Player;

import io.netty.channel.Channel;

/**
 * Helper class for manipule sending and receiving packets via particular channel.
 */
public interface ChannelWrapper
{
    /**
     * @return channel handle for this wrapper.
     */
    Channel getChannel();
    
    /**
     * @return a player instance associated for that channel, note that method will return null when login proccess isn't completed.
     */
    @Nullable
    Player getPlayer();
    
    /**
     * @return a player entity instance associated for that channel, note that method will return null when login proccess isn't completed.
     */
    @Nullable
    EntityPlayer getMinecraftPlayer();
    
    /**
     * @return a network manager instance for this channel.
     */
    NetworkManager getNetworkManager();
    
    /**
     * Behaves exactly the same as if the packet was sent by the server
     * @param packet to send
     */
    void sendPacket(Packet<?> packet);
    
    /**
     * Write packet directly to the channel, so any of packet events won't be called.
     * @param packet to write
     */
    void writePacket(Packet<?> packet);
    
    /**
     * Pretends receiving packet from client.
     * @param packet to 
     */
    void receivePacket(Packet<?> packet);
    
    /**
     * Handle serverbound packet.
     * Similar to receivePacket but that method doesn't trigger any events.
     * @param packet
     */
    void handlePacket(Packet<?> packet);
}
