package pl.north93.zgame.antycheat.event.source;

import net.minecraft.server.v1_12_R1.PacketPlayInCustomPayload;
import net.minecraft.server.v1_12_R1.PacketPlayInFlying;
import net.minecraft.server.v1_12_R1.PacketPlayInFlying.PacketPlayInLook;
import net.minecraft.server.v1_12_R1.PacketPlayInFlying.PacketPlayInPosition;
import net.minecraft.server.v1_12_R1.PacketPlayInFlying.PacketPlayInPositionLook;
import net.minecraft.server.v1_12_R1.PacketPlayInUseEntity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import io.netty.buffer.ByteBuf;
import pl.north93.zgame.antycheat.event.impl.ClientMoveTimelineEvent;
import pl.north93.zgame.antycheat.event.impl.InteractWithEntityTimelineEvent;
import pl.north93.zgame.antycheat.event.impl.InteractWithEntityTimelineEvent.EntityAction;
import pl.north93.zgame.antycheat.event.impl.PluginMessageTimelineEvent;
import pl.north93.zgame.antycheat.timeline.Timeline;
import pl.north93.zgame.antycheat.timeline.impl.TimelineManager;
import pl.north93.zgame.antycheat.utils.handle.WorldHandle;
import pl.north93.zgame.antycheat.utils.location.RichEntityLocation;
import pl.north93.zgame.api.bukkit.protocol.PacketEvent;
import pl.north93.zgame.api.bukkit.protocol.PacketHandler;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class AntyCheatPacketListener
{
    @Inject
    private TimelineManager timelineManager;

    @Bean
    private AntyCheatPacketListener()
    {
    }

    @PacketHandler
    public void onAsyncLookPacket(final PacketEvent<PacketPlayInLook> event)
    {
        final Player player = event.getPlayer();

        final ClientMoveTimelineEvent moveTimelineEvent = this.createMoveTimelineEvent(player, event.getPacket());
        this.timelineManager.pushEventForPlayer(player, moveTimelineEvent);
    }

    @PacketHandler
    public void onAsyncPositionPacket(final PacketEvent<PacketPlayInPosition> event)
    {
        final Player player = event.getPlayer();

        final ClientMoveTimelineEvent moveTimelineEvent = this.createMoveTimelineEvent(player, event.getPacket());
        this.timelineManager.pushEventForPlayer(player, moveTimelineEvent);
    }

    @PacketHandler
    public void onAsyncPositionLookPacket(final PacketEvent<PacketPlayInPositionLook> event)
    {
        final Player player = event.getPlayer();

        final ClientMoveTimelineEvent moveTimelineEvent = this.createMoveTimelineEvent(player, event.getPacket());
        this.timelineManager.pushEventForPlayer(player, moveTimelineEvent);
    }

    @PacketHandler
    public void onAsyncUseEntityPacket(final PacketEvent<PacketPlayInUseEntity> event)
    {
        final Player player = event.getPlayer();
        final PacketPlayInUseEntity packet = event.getPacket();

        final WorldHandle worldHandle = WorldHandle.of(player.getWorld());
        final Entity entity = worldHandle.getBukkitEntityById(packet.getEntityId());
        final EntityAction entityAction = EntityAction.values()[packet.a().ordinal()];

        final InteractWithEntityTimelineEvent timelineEvent = new InteractWithEntityTimelineEvent(player, entity, entityAction);
        this.timelineManager.pushEventForPlayer(player, timelineEvent);
    }

    @PacketHandler
    public void onAsyncCustomPayloadPacket(final PacketEvent<PacketPlayInCustomPayload> event)
    {
        final Player player = event.getPlayer();

        final PluginMessageTimelineEvent timelineEvent = this.createPluginMessageTimelineEvent(player, event.getPacket());
        this.timelineManager.pushEventForPlayer(player, timelineEvent);
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
