package pl.north93.zgame.api.bukkit.protocol;

import org.bukkit.entity.Player;

import net.minecraft.server.v1_12_R1.Packet;

public interface PacketEvent<P extends Packet<?>>
{
    /**
     * @return packet instance
     */
    P getPacket();
    
    /**
     * Sets a new instance of packet. If new packet is null then no packet will be sent/received
     * 
     * @param newPacket a new instance of packet
     */
    void setPacket(P newPacket);
    
    /**
     * @return a cancelled flag
     */
    boolean isCancelled();
    
    /**
     * Sets a state of cancelled flag. If cancelled flag is true then no packet will be sent/received
     * @param flag
     */
    void setCancelled(boolean flag);
    
    /**
     * @return a channel wrapper instance associated with this event
     */
    ChannelWrapper getChannel();
    
    /**
     * The same as getChannel().getPlayer()
     * Note that this method returns null before player login is completed.
     */
    Player getPlayer();
    
    /**
     * @return true whenether packet instance associated with this event is sent to client (clientbound)
     */
    boolean isClientbound();
    
    /**
     * @return true whenether this event is handled in main server thread
     */
    boolean isSync();
    
    /**
     * Registers a new intent.
     * Similar concept to BungeeCord's async event intents.
     * 
     * When all registered intents will be completed then server continue sending/receiving a packet
     */
    Intent registerIntent();
    
    public interface Intent
    {
        /**
         * Completes an intent.
         */
        void complete();
    }
}
