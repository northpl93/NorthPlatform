package pl.north93.northplatform.api.minigame.server.gamehost.region.impl;

import java.util.Set;

import com.google.common.collect.Sets;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

class MoveListener implements Listener
{
    private final RegionManagerImpl manager;

    public MoveListener(final RegionManagerImpl manager)
    {
        this.manager = manager;
    }

    @EventHandler
    public void onTeleport(final PlayerTeleportEvent event)
    {
        final Location from = event.getFrom();
        final Location to = event.getTo();

        final INorthPlayer player = INorthPlayer.wrap(event.getPlayer());
        this.handleRegionDiff(player, from, to);
    }

    @EventHandler
    public void onMove(final PlayerMoveEvent event)
    {
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())
        {
            return;
        }

        final INorthPlayer player = INorthPlayer.wrap(event.getPlayer());
        this.handleMovement(player, from, to);
    }

    private void handleMovement(final INorthPlayer player, final Location from, final Location to)
    {
        if (this.isMoreThanBlock(from, to))
        {
            final Location half = new Location(from.getWorld(), (from.getX() + to.getX()) / 2d, (from.getY() + to.getY()) / 2d, (from.getZ() + to.getZ()) / 2d);
            this.handleMovement(player, from, half);
            this.handleMovement(player, half, to);
            return;
        }

        this.handleRegionDiff(player, from, to);
    }

    @SuppressWarnings("unchecked")
    private void handleRegionDiff(final INorthPlayer player, final Location from, final Location to)
    {
        final Set<TrackedRegionImpl> regionsFrom = (Set) this.manager.getRegions(from);
        final Set<TrackedRegionImpl> regionsTo = (Set) this.manager.getRegions(to);

        final Sets.SetView<TrackedRegionImpl> entered = Sets.difference(regionsTo, regionsFrom);
        final Sets.SetView<TrackedRegionImpl> exited = Sets.difference(regionsFrom, regionsTo);

        for (final TrackedRegionImpl enteredRegion : entered)
        {
            enteredRegion.fireEntered(player);
        }

        for (final TrackedRegionImpl exitedRegion : exited)
        {
            exitedRegion.fireExited(player);
        }
    }

    private boolean isMoreThanBlock(final Location l1, final Location l2)
    {
        return Math.abs(l1.getBlockX() - l2.getBlockX()) > 1 || Math.abs(l1.getBlockY() - l2.getBlockY()) > 1 || Math.abs(l1.getBlockZ() - l2.getBlockZ()) > 1;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
