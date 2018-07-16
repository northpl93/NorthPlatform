package pl.north93.zgame.api.bukkit.protocol.impl;

import java.util.Optional;

import org.bukkit.entity.Player;

import io.netty.channel.Channel;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.NetworkManager;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketListener;
import net.minecraft.server.v1_12_R1.PlayerConnection;

import pl.north93.zgame.api.bukkit.protocol.ChannelWrapper;

public class ChannelWrapperImpl implements ChannelWrapper
{
    private final Channel channel;
    
    ChannelWrapperImpl(Channel channel)
    {
        this.channel = channel;
    }

    @Override
    public Channel getChannel()
    {
        return channel;
    }

    @Override
    public Player getPlayer()
    {
        return Optional.ofNullable(getMinecraftPlayer()).map(EntityPlayer::getBukkitEntity).orElse(null);
    }

    @Override
    public EntityPlayer getMinecraftPlayer()
    {
        PacketListener packetListener = getNetworkManager().i(); // should be getPacketListener()
        if ( packetListener instanceof PlayerConnection )
        {
            return ((PlayerConnection) packetListener).player;
        }
        
        return null;
    }

    @Override
    public NetworkManager getNetworkManager()
    {
        return channel.pipeline().get(NetworkManager.class);
    }

    @Override
    public void sendPacket(Packet<?> packet)
    {
        getNetworkManager().sendPacket(packet);
    }

    @Override
    public void writePacket(Packet<?> packet)
    {
        channel.eventLoop().execute(() -> channel.pipeline().context(NorthLegacyEventHandler.class).write(packet));
    }

    @Override
    public void receivePacket(Packet<?> packet)
    {
        channel.eventLoop().execute(() -> channel.pipeline().context("encoder").fireChannelRead(packet));
    }

    @Override
    public void handlePacket(Packet<?> packet)
    {
        channel.eventLoop().execute(() -> channel.pipeline().context(NorthChannelHandler.class).fireChannelRead(packet));
    }
}
