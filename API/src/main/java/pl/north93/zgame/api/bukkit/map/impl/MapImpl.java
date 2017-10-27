package pl.north93.zgame.api.bukkit.map.impl;

import static pl.north93.zgame.api.bukkit.utils.nms.EntityTrackerHelper.getTrackerEntry;
import static pl.north93.zgame.api.bukkit.utils.nms.EntityTrackerHelper.observeTracker;
import static pl.north93.zgame.api.bukkit.utils.nms.EntityTrackerHelper.toNmsEntity;


import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.EntityTrackerEntry;

import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javafx.collections.ObservableMap;
import pl.north93.zgame.api.bukkit.map.IMap;

public class MapImpl implements IMap
{
    private final MapController controller;
    private final BoardImpl     board;
    private final ItemFrame     itemFrame;

    public MapImpl(final MapController controller, final BoardImpl board, final ItemFrame itemFrame)
    {
        this.controller = controller;
        this.board = board;
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
        return this.itemFrame.getEntityId();
    }

    /**
     * Sprawdza czy ta mapa jest sledzona przez podanego gracza.
     * Inaczej mowiac czy jest w zasiegu danego gracza.
     *
     * @param player
     * @return
     */
    public boolean isTrackedBy(final Player player)
    {
        final EntityTrackerEntry trackerEntry = getTrackerEntry(toNmsEntity(player));
        for (final EntityPlayer trackedPlayer : trackerEntry.trackedPlayers)
        {
            if (trackedPlayer.getBukkitEntity() == player)
            {
                return true;
            }
        }
        return false;
    }

    private void setupTracker()
    {
        final ObservableMap<EntityPlayer, Boolean> tracker = observeTracker(toNmsEntity(this.itemFrame));
        tracker.addListener(this.controller.trackerListener(this));
    }

    @Override
    public Location getLocation()
    {
        return this.itemFrame.getLocation();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("itemFrame", this.itemFrame).toString();
    }
}
