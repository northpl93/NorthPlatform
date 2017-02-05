package pl.north93.zgame.skyblock.server.listeners.lobby;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class Launchpad implements Listener
{
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMoveLaunchpad(final PlayerMoveEvent event)
    {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ())
        {
            return;
        }

        if (to.getBlock().getType().equals(Material.STONE_PLATE) && to.clone().add(0, - 1, 0).getBlock().getType().equals(Material.GOLD_BLOCK))
        {
            event.getPlayer().setVelocity(this.calcVector(from, to));
        }
    }

    @SuppressWarnings("MagicNumber")
    private Vector calcVector(final Location from, final Location to)
    {
        int x = 0, z = 0;
        if (from.getBlockX() > to.getBlockX())
        {
            x = -8;
        }
        else if (from.getBlockX() < to.getBlockX())
        {
            x = 8;
        }

        if (from.getBlockZ() > to.getBlockZ())
        {
            z = -8;
        }
        else if (from.getBlockZ() < to.getBlockZ())
        {
            z = 8;
        }
        return new Vector(x, 1.5, z);
    }
}
