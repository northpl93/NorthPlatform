package pl.arieals.api.minigame.server.gamehost.region.impl;

import java.util.Set;

import com.google.common.collect.Sets;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

class MoveListener implements Listener
{
    private final RegionManagerImpl manager;

    public MoveListener(final RegionManagerImpl manager)
    {
        this.manager = manager;
    }

    @SuppressWarnings("unchecked")
    @EventHandler
    public void onMove(final PlayerMoveEvent event)
    {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())
        {
            return;
        }

        final Set<TrackedRegionImpl> regionsFrom = (Set) this.manager.getRegions(from);
        final Set<TrackedRegionImpl> regionsTo = (Set) this.manager.getRegions(to);

        final Sets.SetView<TrackedRegionImpl> entered = Sets.difference(regionsFrom, regionsTo);
        final Sets.SetView<TrackedRegionImpl> exited = Sets.difference(regionsTo, regionsFrom);

        for (final TrackedRegionImpl enteredRegion : entered)
        {
            enteredRegion.fireEntered(event.getPlayer());
        }

        for (final TrackedRegionImpl exitedRegion : exited)
        {
            exitedRegion.fireExited(event.getPlayer());
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
