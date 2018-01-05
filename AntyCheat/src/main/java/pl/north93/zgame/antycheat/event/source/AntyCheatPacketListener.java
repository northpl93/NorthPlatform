package pl.north93.zgame.antycheat.event.source;

import net.minecraft.server.v1_10_R1.Packet;
import net.minecraft.server.v1_10_R1.PacketPlayInCustomPayload;
import net.minecraft.server.v1_10_R1.PacketPlayInFlying;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import pl.north93.zgame.antycheat.event.impl.PlayerMoveTimelineEvent;
import pl.north93.zgame.antycheat.event.impl.PluginMessageTimelineEvent;
import pl.north93.zgame.antycheat.timeline.impl.TimelineManager;
import pl.north93.zgame.api.bukkit.packets.event.AsyncPacketInEvent;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class AntyCheatPacketListener implements AutoListener
{
    @Inject
    private TimelineManager timelineManager;

    @EventHandler
    public void onAsyncPacketIn(final AsyncPacketInEvent event)
    {
        final Player player = event.getPlayer();
        final Packet packet = event.getPacket();

        if (packet instanceof PacketPlayInFlying)
        {
            final PacketPlayInFlying packetPlayInFlying = (PacketPlayInFlying) packet;

            final PlayerMoveTimelineEvent moveTimelineEvent = this.createMoveTimelineEvent(player, packetPlayInFlying);
            this.timelineManager.pushEventForPlayer(player, moveTimelineEvent);
        }
        else if (packet instanceof PacketPlayInCustomPayload)
        {
            final PacketPlayInCustomPayload customPayload = (PacketPlayInCustomPayload) packet;

            final String channel = customPayload.a();
            final byte[] data = customPayload.b().array();

            final PluginMessageTimelineEvent pluginMessageTimelineEvent = new PluginMessageTimelineEvent(player, channel, data);
            this.timelineManager.pushEventForPlayer(player, pluginMessageTimelineEvent);
        }
    }

    // tworzy PlayerMoveTimelineEvent na podstawie obiektu gracza i pakietów od movementu
    private PlayerMoveTimelineEvent createMoveTimelineEvent(final Player player, final PacketPlayInFlying packetPlayInFlying)
    {
        final boolean oldOnGround = ((CraftPlayer) player).getHandle().onGround;
        final Location oldLocation = player.getLocation();

        final boolean newOnGround = packetPlayInFlying.a();
        final Location newLocation = new Location(
                oldLocation.getWorld(),
                packetPlayInFlying.a(oldLocation.getX()),
                packetPlayInFlying.b(oldLocation.getY()),
                packetPlayInFlying.c(oldLocation.getZ()),
                packetPlayInFlying.a(oldLocation.getYaw()),
                packetPlayInFlying.b(oldLocation.getPitch()));

        return new PlayerMoveTimelineEvent(player, oldLocation, oldOnGround, newLocation, newOnGround);
    }
}
