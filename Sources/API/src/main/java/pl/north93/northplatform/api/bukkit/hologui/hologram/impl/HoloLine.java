package pl.north93.northplatform.api.bukkit.hologui.hologram.impl;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;


import static pl.north93.northplatform.api.bukkit.utils.nms.EntityTrackerHelper.toNmsPlayer;


import java.util.Locale;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EntityTrackerEntry;
import net.minecraft.server.v1_12_R1.WorldServer;

import com.google.common.base.Preconditions;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.hologui.hologram.HologramRenderContext;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.IBukkitExecutor;
import pl.north93.northplatform.api.bukkit.utils.nms.EntityMetaPacketHelper;
import pl.north93.northplatform.api.bukkit.utils.nms.EntityTrackerHelper;

final class HoloLine
{
    private final HologramImpl hologram;
    private final int lineNo;
    private ArmorStand armorStand;

    public HoloLine(final HologramImpl hologram, final int lineNo)
    {
        this.hologram = hologram;
        this.lineNo = lineNo;
    }

    public void cleanup()
    {
        this.destroyArmorStand();
    }

    /*default*/ void createArmorStand()
    {
        final double deltaY = this.lineNo * this.hologram.getLinesSpacing(); // 0-na samym dole, rosnace wyzej
        final Location myLoc = this.hologram.getLocation().clone().add(0, deltaY, 0);

        final CraftWorld craftWorld = (CraftWorld) myLoc.getWorld();
        final WorldServer nmsWorld = craftWorld.getHandle();

        final HologramArmorStand entityArmorStand = new HologramArmorStand(nmsWorld, myLoc.getX(), myLoc.getY(), myLoc.getZ(), this);
        this.armorStand = (ArmorStand) entityArmorStand.getBukkitEntity();
        this.setupArmorStand(this.armorStand);

        // konfigurujemy widocznosc
        this.hologram.setupVisibility(this.armorStand);

        // spawnujemy entity
        nmsWorld.addEntity(entityArmorStand, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    private void destroyArmorStand()
    {
        Preconditions.checkState(this.armorStand != null, "ArmorStand doesn't exist");

        final CraftArmorStand craftArmorStand = (CraftArmorStand) this.armorStand;
        final CraftWorld world = (CraftWorld) craftArmorStand.getWorld();

        // to spowoduje zabicie entity i natychmiastowe usuniecie go z listy
        world.getHandle().removeEntity(craftArmorStand.getHandle());
    }

    // wysyla pakiet aktualizujacy gdy gracz zacznie trackowac entity
    /*default*/ void playerStartedTracking(final Player bukkitPlayer)
    {
        final INorthPlayer northPlayer = INorthPlayer.wrap(bukkitPlayer);
        final EntityPlayer entityPlayer = northPlayer.getCraftPlayer().getHandle();

        final IBukkitExecutor bukkitExecutor = this.hologram.getHologramManager().getBukkitExecutor();
        bukkitExecutor.syncLater(1, () -> this.sendUpdateTo(entityPlayer, northPlayer.getMyLocale()));
    }

    /*default*/ boolean isTrackedBy(final Player player)
    {
        final Entity entityArmorStand = EntityTrackerHelper.toNmsEntity(this.armorStand);
        final EntityPlayer entityPlayer = toNmsPlayer(player);

        final EntityTrackerEntry trackerEntry = EntityTrackerHelper.getTrackerEntry(entityArmorStand);
        return trackerEntry.trackedPlayers.contains(entityPlayer);
    }

    /*default*/ void sendUpdateTo(final Player player)
    {
        final INorthPlayer northPlayer = INorthPlayer.wrap(player);
        final EntityPlayer entityPlayer = northPlayer.getCraftPlayer().getHandle();

        this.sendUpdateTo(entityPlayer, northPlayer.getMyLocale());
    }

    /*default*/ void broadcastUpdate()
    {
        final EntityTrackerEntry trackerEntry = EntityTrackerHelper.getTrackerEntry(EntityTrackerHelper.toNmsEntity(this.armorStand));
        if (trackerEntry == null)
        {
            // tak moze byc w przypadku gdy hologram zespawnuje sie poza zaladowanym terenem
            return;
        }

        for (final EntityPlayer trackedPlayer : trackerEntry.trackedPlayers)
        {
            final INorthPlayer northPlayer = INorthPlayer.wrap(trackedPlayer.getBukkitEntity());
            this.sendUpdateTo(trackedPlayer, northPlayer.getMyLocale());
        }
    }

    private void sendUpdateTo(final EntityPlayer entityPlayer, final Locale locale)
    {
        final HologramRenderContext context = new HologramRenderContext(this.hologram, entityPlayer.getBukkitEntity(), locale);
        final String newText = this.hologram.getLine(context, this.lineNo);

        final EntityMetaPacketHelper packetHelper = new EntityMetaPacketHelper(this.armorStand.getEntityId());
        // 2=custom name http://wiki.vg/Entities#Entity
        packetHelper.addMeta(2, EntityMetaPacketHelper.MetaType.STRING, newText);
        // 3=custom name visible
        packetHelper.addMeta(3, EntityMetaPacketHelper.MetaType.BOOLEAN, isNotEmpty(newText));

        entityPlayer.playerConnection.networkManager.channel.writeAndFlush(packetHelper.complete());
    }

    private void setupArmorStand(final ArmorStand armorStand)
    {
        armorStand.setAI(false);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setSmall(true);
        armorStand.setMarker(true);
        armorStand.setSilent(true);
        armorStand.setInvulnerable(true);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}