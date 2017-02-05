package pl.north93.zgame.api.bukkit.packets;

import net.minecraft.server.v1_10_R1.Packet;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.netty.channel.Channel;
import pl.north93.zgame.api.bukkit.event.AsyncPacketInEvent;
import pl.north93.zgame.api.bukkit.event.AsyncPacketOutEvent;
import pl.north93.zgame.api.bukkit.packets.tinyprotocol.TinyProtocol;

public class PacketsHandler extends TinyProtocol
{
    public PacketsHandler(final Plugin plugin)
    {
        super(plugin);
    }

    @Override
    public Object onPacketOutAsync(final Player reciever, final Channel channel, final Object packet)
    {
        if (! (packet instanceof Packet))
        {
            return packet;
        }
        final AsyncPacketOutEvent event = new AsyncPacketOutEvent(reciever, (Packet) packet);
        this.plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled())
        {
            return null;
        }
        return event.getPacket();
    }

    @Override
    public Object onPacketInAsync(final Player sender, final Channel channel, final Object packet)
    {
        if (! (packet instanceof Packet))
        {
            return packet;
        }
        final AsyncPacketInEvent event = new AsyncPacketInEvent(sender, (Packet) packet);
        this.plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled())
        {
            return null;
        }
        return event.getPacket();
    }
}
