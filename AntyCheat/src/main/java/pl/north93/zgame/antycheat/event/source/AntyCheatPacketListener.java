package pl.north93.zgame.antycheat.event.source;

import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketPlayInCustomPayload;
import net.minecraft.server.v1_12_R1.PacketPlayInFlying;
import net.minecraft.server.v1_12_R1.PacketPlayInUseEntity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import pl.north93.zgame.antycheat.event.impl.ClientMoveTimelineEvent;
import pl.north93.zgame.antycheat.event.impl.InteractWithEntityTimelineEvent;
import pl.north93.zgame.antycheat.event.impl.InteractWithEntityTimelineEvent.EntityAction;
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
        final Packet packet = event.getPacket();

        //Bukkit.broadcastMessage(packet + "");
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

            final String channel = customPayload.a();
            //final byte[] data = customPayload.b().array(); // todo Caused by: java.lang.UnsupportedOperationException: direct buffer

            //final PluginMessageTimelineEvent pluginMessageTimelineEvent = new PluginMessageTimelineEvent(player, channel, data);
            //this.timelineManager.pushEventForPlayer(player, pluginMessageTimelineEvent);
        }
    }

    // tworzy PlayerMoveTimelineEvent na podstawie obiektu gracza i pakietów od movementu
    private ClientMoveTimelineEvent createMoveTimelineEvent(final Player player, final PacketPlayInFlying packetPlayInFlying)
    {
        final boolean oldOnGround = ((CraftPlayer) player).getHandle().onGround;
        final RichEntityLocation oldLocation = new RichEntityLocation(player, player.getLocation());

        final boolean newOnGround = packetPlayInFlying.a();
        final Location newLocation = new Location(
                oldLocation.getWorld(),
                packetPlayInFlying.a(oldLocation.getX()),
                packetPlayInFlying.b(oldLocation.getY()),
                packetPlayInFlying.c(oldLocation.getZ()),
                packetPlayInFlying.a(oldLocation.getYaw()) % 360F,
                packetPlayInFlying.b(oldLocation.getPitch()) % 360F);

        return new ClientMoveTimelineEvent(player, oldLocation, oldOnGround, new RichEntityLocation(player, newLocation), newOnGround);
    }
}
