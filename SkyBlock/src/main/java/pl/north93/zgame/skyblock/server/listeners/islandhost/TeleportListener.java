package pl.north93.zgame.skyblock.server.listeners.islandhost;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.management.IslandHostManager;
import pl.north93.zgame.skyblock.server.world.Island;
import pl.north93.zgame.skyblock.server.world.WorldManager;

public class TeleportListener implements Listener
{
    @Inject
    private SkyBlockServer server;

    private boolean canTeleport(final Player player, final Location location)
    {
        final WorldManager manager = this.server.<IslandHostManager>getServerManager().getWorldManager(location.getWorld());
        if (manager == null)
        {
            return true;
        }

        final Island island = manager.getIslands().getByChunk(location.getChunk());
        return island == null || island.isAcceptingVisits() || ! island.getLocation().isInside(location) || island.canBuild(player.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnderPearl(final PlayerTeleportEvent event)
    {
        final Material itemToRestore;
        final TeleportCause cause = event.getCause();
        if (cause.equals(TeleportCause.ENDER_PEARL))
        {
            itemToRestore = Material.ENDER_PEARL;
        }
        else if (cause.equals(TeleportCause.CHORUS_FRUIT))
        {
            itemToRestore = Material.CHORUS_FRUIT;
        }
        else
        {
            return;
        }

        if (! this.canTeleport(event.getPlayer(), event.getTo()))
        {
            event.getPlayer().getInventory().addItem(new ItemStack(itemToRestore, 1));
            event.setCancelled(true);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
