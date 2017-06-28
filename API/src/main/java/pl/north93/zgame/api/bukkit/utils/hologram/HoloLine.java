package pl.north93.zgame.api.bukkit.utils.hologram;

import static pl.north93.zgame.api.bukkit.utils.nms.EntityTrackerHelper.getTrackerEntry;
import static pl.north93.zgame.api.bukkit.utils.nms.EntityTrackerHelper.observeTracker;


import net.minecraft.server.v1_10_R1.EntityPlayer;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import pl.north93.zgame.api.bukkit.utils.nms.EntityMetaPacketHelper;

final class HoloLine
{
    private final HologramImpl hologram;
    private final int          lineNo;
    private final ArmorStand   armorStand;
    private IHologramLine      lastLine;

    public HoloLine(final HologramImpl hologram, final int lineNo)
    {
        this.hologram = hologram;
        this.lineNo = lineNo;
        final Location myLoc = hologram.getLocation().clone().add(0, -lineNo * 0.3, 0);

        this.armorStand = (ArmorStand) hologram.getLocation().getWorld().spawnEntity(myLoc, EntityType.ARMOR_STAND);
        this.armorStand.setSmall(true);
        this.armorStand.setGravity(false);
        this.armorStand.setVisible(false);
        this.armorStand.setCustomNameVisible(true);

        // Musimy sledzic aktualizacje w trackerze zeby wysylac nowa nazwe
        final ObservableMap<EntityPlayer, Boolean> observableTrackerList = observeTracker(((CraftArmorStand) this.armorStand).getHandle());

        observableTrackerList.addListener((MapChangeListener<? super EntityPlayer, ? super Boolean>) change ->
        {
            final EntityPlayer key = change.getKey();
            if (change.wasAdded() && this.lastLine != null)
            {
                final String newText = this.lastLine.render(this.hologram, key.getBukkitEntity());

                final EntityMetaPacketHelper packetHelper = new EntityMetaPacketHelper(this.armorStand.getEntityId());
                packetHelper.addMeta(2, EntityMetaPacketHelper.MetaType.STRING, newText); // 2=custom name http://wiki.vg/Entities#Entity
                key.playerConnection.networkManager.channel.writeAndFlush(packetHelper.complete());
            }
        });

    }

    public int getLineNo()
    {
        return this.lineNo;
    }

    public void setText(final IHologramLine line)
    {
        if (line.equals(this.lastLine))
        {
            return;
        }
        this.lastLine = line;

        for (final EntityPlayer player : getTrackerEntry(((CraftArmorStand) this.armorStand).getHandle()).trackedPlayers)
        {
            final String newText = line.render(this.hologram, player.getBukkitEntity());

            final EntityMetaPacketHelper packetHelper = new EntityMetaPacketHelper(this.armorStand.getEntityId());
            packetHelper.addMeta(2, EntityMetaPacketHelper.MetaType.STRING, newText); // 2=custom name http://wiki.vg/Entities#Entity
            player.playerConnection.networkManager.channel.write(packetHelper.complete());
        }
    }

    public void cleanup()
    {
        this.armorStand.remove();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
