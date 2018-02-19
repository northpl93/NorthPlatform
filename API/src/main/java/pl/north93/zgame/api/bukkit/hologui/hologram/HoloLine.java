package pl.north93.zgame.api.bukkit.hologui.hologram;

import static pl.north93.zgame.api.bukkit.utils.nms.EntityTrackerHelper.getTrackerEntry;
import static pl.north93.zgame.api.bukkit.utils.nms.EntityTrackerHelper.observeTracker;


import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EntityTrackerEntry;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.World;
import net.minecraft.server.v1_12_R1.WorldServer;

import com.google.common.base.Preconditions;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.entity.CreatureSpawnEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.utils.nms.EntityMetaPacketHelper;

final class HoloLine
{
    private final HologramImpl hologram;
    private final int          lineNo;
    private ArmorStand         armorStand;
    private IHologramLine      lastLine;

    public HoloLine(final HologramImpl hologram, final int lineNo)
    {
        this.hologram = hologram;
        this.lineNo = lineNo;
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

        if (this.armorStand == null)
        {
            this.createArmorStand();
        }
        else
        {
            this.broadcastUpdate();
        }
    }

    public void cleanup()
    {
        this.destroyArmorStand();
    }

    private void createArmorStand()
    {
        Preconditions.checkState(this.armorStand == null, "ArmorStand already created");
        Preconditions.checkNotNull(this.lastLine, "Tried to create Armorstand when lastLine is null");

        final double deltaY;
        if (this.hologram.isLowerLocation())
        {
            deltaY = this.lineNo * this.hologram.getLinesSpacing();
        }
        else
        {
            deltaY = - this.lineNo * this.hologram.getLinesSpacing();
        }
        final Location myLoc = this.hologram.getLocation().clone().add(0, deltaY, 0);

        final CraftWorld craftWorld = (CraftWorld) myLoc.getWorld();
        final WorldServer nmsWorld = craftWorld.getHandle();

        final HologramArmorStand entityArmorStand = new HologramArmorStand(nmsWorld, myLoc.getX(), myLoc.getY(), myLoc.getZ());
        this.armorStand = (ArmorStand) entityArmorStand.getBukkitEntity();
        this.setupArmorStand();

        // konfigurujemy widocznosc
        this.hologram.setupVisibility(this.armorStand);

        // spawnujemy entity
        nmsWorld.addEntity(entityArmorStand, CreatureSpawnEvent.SpawnReason.CUSTOM);

        // Musimy sledzic aktualizacje w trackerze zeby wysylac nowa nazwe
        this.setupEntityTracker();
    }

    private void destroyArmorStand()
    {
        Preconditions.checkState(this.armorStand != null, "ArmorStand doesn't exist");

        final CraftArmorStand craftArmorStand = (CraftArmorStand) this.armorStand;
        final CraftWorld world = (CraftWorld) craftArmorStand.getWorld();

        // to spowoduje zabicie entity i natychmiastowe usuniecie go z listy
        world.getHandle().removeEntity(craftArmorStand.getHandle());
    }

    private void setupEntityTracker()
    {
        final EntityArmorStand entityArmorStand = ((CraftArmorStand) this.armorStand).getHandle();

        final ObservableMap<EntityPlayer, Boolean> observableTrackerList = observeTracker(entityArmorStand);
        observableTrackerList.addListener(this::playerStartedTracking);

        final Set<EntityPlayer> entityPlayers = observableTrackerList.keySet();
        if (! entityPlayers.isEmpty())
        {
            // jesli lista juz trackujacych graczy nie jest pusta to untrackujemy hologram
            // u wszystkich graczy, aby nazwa sie poprawnie wyslala
            final EntityTrackerEntry trackerEntry = getTrackerEntry(entityArmorStand);
            for (final EntityPlayer entityPlayer : new HashSet<>(entityPlayers))
            {
                trackerEntry.a(entityPlayer); // untrack
            }
        }
    }

    // wysyla pakiet aktualizujacy gdy gracz zacznie trackowac entity
    private void playerStartedTracking(final MapChangeListener.Change<? extends EntityPlayer, ? extends Boolean> change)
    {
        final EntityPlayer entityPlayer = change.getKey();

        if (change.wasAdded() && change.getValueRemoved() == null && this.lastLine != null)
        {
            final INorthPlayer northPlayer = INorthPlayer.wrap(entityPlayer.getBukkitEntity());

            // Uruchamiamy wysylanie asynchronicznie bo getMyLocale moze wykonac zapytanie do redisa
            this.hologram.getBukkitExecutor().async(() -> this.sendUpdateTo(entityPlayer, northPlayer.getMyLocale()));
        }
    }

    private void broadcastUpdate()
    {
        final EntityArmorStand entityArmorStand = ((CraftArmorStand) this.armorStand).getHandle();
        final EntityTrackerEntry trackerEntry = getTrackerEntry(entityArmorStand);

        for (final EntityPlayer trackedPlayer : trackerEntry.trackedPlayers)
        {
            final INorthPlayer northPlayer = INorthPlayer.wrap(trackedPlayer.getBukkitEntity());
            this.sendUpdateTo(trackedPlayer, northPlayer.getMyLocale());
        }
    }

    private void sendUpdateTo(final EntityPlayer entityPlayer, final Locale locale)
    {
        Preconditions.checkNotNull(this.lastLine, "Tried to update ArmorStand name when lastLine is null");
        final EntityMetaPacketHelper packetHelper = new EntityMetaPacketHelper(this.armorStand.getEntityId());

        final String newText = this.lastLine.render(this.hologram, entityPlayer.getBukkitEntity(), locale);
        // 2=custom name http://wiki.vg/Entities#Entity
        packetHelper.addMeta(2, EntityMetaPacketHelper.MetaType.STRING, newText);

        entityPlayer.playerConnection.networkManager.channel.writeAndFlush(packetHelper.complete());
    }

    private void setupArmorStand()
    {
        this.armorStand.setAI(false);
        this.armorStand.setGravity(false);
        this.armorStand.setVisible(false);
        this.armorStand.setSmall(true);
        this.armorStand.setMarker(true);
        this.armorStand.setSilent(true);
        this.armorStand.setInvulnerable(true);
        this.armorStand.setCustomNameVisible(true);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

final class HologramArmorStand extends EntityArmorStand
{
    public HologramArmorStand(final World world)
    {
        super(world);
    }

    public HologramArmorStand(final World world, final double d0, final double d1, final double d2)
    {
        this(world);
        this.setPosition(d0, d1, d2);
    }

    @Override
    public boolean c(final NBTTagCompound nbttagcompound)
    {
        // metoda zapisująca entity do NBTTaga, BEZ sprawdzania passengera
        // false oznacza ze compound nie zostanie dodany do swiata, a wiec my tak chcemy
        return false;
    }

    @Override
    public boolean d(final NBTTagCompound nbttagcompound)
    {
        // metoda zapisująca entity do NBTTaga, Z sprawdzaniem passengera
        return false;
    }
}