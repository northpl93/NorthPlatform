package pl.north93.zgame.antycheat.event.source;

import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketPlayInCustomPayload;
import net.minecraft.server.v1_12_R1.PacketPlayInFlying;
import net.minecraft.server.v1_12_R1.PacketPlayInUseEntity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import io.netty.buffer.ByteBuf;
import pl.north93.zgame.antycheat.event.impl.ClientMoveTimelineEvent;
import pl.north93.zgame.antycheat.event.impl.InteractWithEntityTimelineEvent;
import pl.north93.zgame.antycheat.event.impl.InteractWithEntityTimelineEvent.EntityAction;
import pl.north93.zgame.antycheat.event.impl.PluginMessageTimelineEvent;
import pl.north93.zgame.antycheat.timeline.Timeline;
import pl.north93.zgame.antycheat.timeline.impl.TimelineManager;
import pl.north93.zgame.antycheat.utils.handle.WorldHandle;
import pl.north93.zgame.antycheat.utils.location.RichEntityLocation;
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
        if (player == null)
        {
            return;
        }

        final Packet packet = event.getPacket();
        if (packet instanceof PacketPlayInFlying)
        {
            final PacketPlayInFlying packetPlayInFlying = (PacketPlayInFlying) packet;

            final ClientMoveTimelineEvent moveTimelineEvent = this.createMoveTimelineEvent(player, packetPlayInFlying);
            this.timelineManager.pushEventForPlayer(player, moveTimelineEvent);
        }
        else if (packet instanceof PacketPlayInUseEntity)
        {
            final PacketPlayInUseEntity packetPlayInUseEntity = (PacketPlayInUseEntity) packet;

            final WorldHandle worldHandle = WorldHandle.of(player.getWorld());
            final Entity entity = worldHandle.getBukkitEntityById(packetPlayInUseEntity.getEntityId());
            final EntityAction entityAction = EntityAction.values()[packetPlayInUseEntity.a().ordinal()];

            final InteractWithEntityTimelineEvent timelineEvent = new InteractWithEntityTimelineEvent(player, entity, entityAction);
            this.timelineManager.pushEventForPlayer(player, timelineEvent);
        }
        else if (packet instanceof PacketPlayInCustomPayload)
        {
            final PacketPlayInCustomPayload customPayload = (PacketPlayInCustomPayload) packet;

            final PluginMessageTimelineEvent timelineEvent = this.createPluginMessageTimelineEvent(player, customPayload);
            this.timelineManager.pushEventForPlayer(player, timelineEvent);
        }
    }

    // tworzy PlayerMoveTimelineEvent na podstawie obiektu gracza i pakietów od movementu
    private ClientMoveTimelineEvent createMoveTimelineEvent(final Player player, final PacketPlayInFlying packet)
    {
        final Timeline timeline = this.timelineManager.getPlayerTimeline(player);
        final ClientMoveTimelineEvent previousEvent = timeline.getPreviousEvent(ClientMoveTimelineEvent.class);

        final boolean oldOnGround = previousEvent != null && previousEvent.isToOnGround();
        final RichEntityLocation oldLocation = previousEvent == null ? new RichEntityLocation(player, player.getLocation()) : previousEvent.getTo();

        final boolean newOnGround = packet.a();
        final Location newLocation = new Location(
                player.getWorld(),
                packet.a(oldLocation.getX()),
                packet.b(oldLocation.getY()),
                packet.c(oldLocation.getZ()),
                packet.a(oldLocation.getYaw()) % 360F,
                packet.b(oldLocation.getPitch()) % 360F);

        return new ClientMoveTimelineEvent(player, oldLocation, oldOnGround, new RichEntityLocation(player, newLocation), newOnGround);
    }

    private PluginMessageTimelineEvent createPluginMessageTimelineEvent(final Player player, final PacketPlayInCustomPayload packet)
    {
        final String channel = packet.a();
        //final byte[] data = customPayload.b().array(); // Caused by: java.lang.UnsupportedOperationException: direct buffer

        final ByteBuf copiedData = packet.b().copy(); // kopiujemy zeby nie zepsuc pakietu
        final byte[] data = new byte[copiedData.readableBytes()];
        copiedData.readBytes(data);
        copiedData.release();

        return new PluginMessageTimelineEvent(player, channel, data);
    }
}
