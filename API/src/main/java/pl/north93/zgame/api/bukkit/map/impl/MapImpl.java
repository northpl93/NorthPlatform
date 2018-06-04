package pl.north93.zgame.api.bukkit.map.impl;

import static pl.north93.zgame.api.bukkit.utils.nms.EntityTrackerHelper.getTrackerEntry;
import static pl.north93.zgame.api.bukkit.utils.nms.EntityTrackerHelper.observeTracker;
import static pl.north93.zgame.api.bukkit.utils.nms.EntityTrackerHelper.toNmsEntity;


import javax.annotation.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EntityTrackerEntry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftItemFrame;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javafx.collections.ObservableMap;
import pl.north93.zgame.api.bukkit.map.IMap;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;

class MapImpl implements IMap
{
    private final MapController controller;
    private final BoardImpl     board;
    private final UUID          frameId;
    private       ItemFrame     itemFrame;

    public MapImpl(final MapController controller, final BoardImpl board, final ItemFrame itemFrame)
    {
        this.controller = controller;
        this.board = board;
        this.frameId = itemFrame.getUniqueId();
        this.itemFrame = itemFrame;
        this.setupTracker();
    }

    /**
     * @return tablica do ktorej nalezy ta mapa.
     */
    public BoardImpl getBoard()
    {
        return this.board;
    }

    /**
     * Zwraca ID entity ramki uzywanej przez ta mape.
     *
     * @return ID entity ramki zawierajacej mape.
     */
    public int getFrameEntityId()
    {
        final ItemFrame itemFrame = this.getItemFrame();
        if (itemFrame == null)
        {
            return -1;
        }

        return itemFrame.getEntityId();
    }

    /**
     * Sprawdza czy ta mapa jest sledzona przez podanego gracza.
     * Inaczej mowiac czy jest w zasiegu danego gracza.
     *
     * @param player Gracz ktorego sprawdzamy.
     * @return True jesli mapa jest widoczna u danego gracza.
     */
    public boolean isTrackedBy(final Player player)
    {
        final EntityTrackerEntry trackerEntry = getTrackerEntry(this.getNmsEntity());
        for (final EntityPlayer trackedPlayer : trackerEntry.trackedPlayers)
        {
            if (player.equals(trackedPlayer.getBukkitEntity()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Zwraca liste graczy sledzacych ta mape.
     *
     * @return lista graczy sledzacych ta mape.
     */
    public Collection<Player> getTrackingPlayers()
    {
        final EntityTrackerEntry trackerEntry = getTrackerEntry(this.getNmsEntity());
        return trackerEntry.trackedPlayers.stream().map(EntityPlayer::getBukkitEntity).collect(Collectors.toSet());
    }

    private void setupTracker()
    {
        final ObservableMap<EntityPlayer, Boolean> tracker = observeTracker(toNmsEntity(this.itemFrame));
        tracker.addListener(this.controller.trackerListener(this));

        // renderujemy mape wszystkim juz obecnym graczom
        for (final Player player : this.getTrackingPlayers())
        {
            this.controller.handlePlayerEnter(this, INorthPlayer.wrap(player));
        }
    }

    @Nullable
    private ItemFrame getItemFrame()
    {
        if (this.itemFrame != null && this.itemFrame.isValid())
        {
            return this.itemFrame;
        }

        final ItemFrame newItemFrame = (ItemFrame) Bukkit.getEntity(this.frameId);
        if ((this.itemFrame = newItemFrame) == null)
        {
            return null;
        }

        this.setupTracker();
        return newItemFrame;
    }

    @Nullable
    private Entity getNmsEntity()
    {
        final ItemFrame itemFrame = this.getItemFrame();
        if (itemFrame == null)
        {
            return null;
        }

        return toNmsEntity(itemFrame);
    }

    /**
     * Zabija ramke nalezaca do tej mapy.
     */
    public void cleanup()
    {
        final CraftItemFrame itemFrame = (CraftItemFrame) this.itemFrame;
        if (itemFrame != null)
        {
            itemFrame.getHandle().die();
        }
    }

    @Override
    public Location getLocation()
    {
        final ItemFrame itemFrame = this.getItemFrame();
        if (itemFrame == null)
        {
            return null;
        }

        return this.getItemFrame().getLocation();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("itemFrame", this.itemFrame).toString();
    }
}
