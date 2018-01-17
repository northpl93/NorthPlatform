package pl.north93.zgame.api.bukkit.map.impl;

import static pl.north93.zgame.api.bukkit.utils.nms.EntityTrackerHelper.getTrackerEntry;
import static pl.north93.zgame.api.bukkit.utils.nms.EntityTrackerHelper.observeTracker;
import static pl.north93.zgame.api.bukkit.utils.nms.EntityTrackerHelper.toNmsEntity;


import java.util.Collection;
import java.util.stream.Collectors;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EntityTrackerEntry;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftItemFrame;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javafx.collections.ObservableMap;
import pl.north93.zgame.api.bukkit.map.IMap;

class MapImpl implements IMap
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
     * @param player Gracz ktorego sprawdzamy.
     * @return True jesli mapa jest widoczna u danego gracza.
     */
    public boolean isTrackedBy(final Player player)
    {
        final EntityTrackerEntry trackerEntry = getTrackerEntry(toNmsEntity(this.itemFrame));
        for (final EntityPlayer trackedPlayer : trackerEntry.trackedPlayers)
        {
            if (trackedPlayer.getBukkitEntity() == player)
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
        final EntityTrackerEntry trackerEntry = getTrackerEntry(toNmsEntity(this.itemFrame));
        return trackerEntry.trackedPlayers.stream().map(EntityPlayer::getBukkitEntity).collect(Collectors.toSet());
    }

    private void setupTracker()
    {
        final ObservableMap<EntityPlayer, Boolean> tracker = observeTracker(toNmsEntity(this.itemFrame));
        tracker.addListener(this.controller.trackerListener(this));

        // renderujemy mape wszystkim juz obecnym graczom
        for (final Player player : this.getTrackingPlayers())
        {
            this.controller.handlePlayerEnter(this, (CraftPlayer) player);
        }
    }

    /**
     * Zabija ramke nalezaca do tej mapy.
     */
    public void cleanup()
    {
        final CraftItemFrame itemFrame = (CraftItemFrame) this.itemFrame;
        itemFrame.getHandle().die();
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
