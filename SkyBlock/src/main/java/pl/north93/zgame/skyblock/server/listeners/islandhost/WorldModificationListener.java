package pl.north93.zgame.skyblock.server.listeners.islandhost;

import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.skyblock.api.IslandData;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.management.IslandHostManager;
import pl.north93.zgame.skyblock.server.world.Island;
import pl.north93.zgame.skyblock.server.world.WorldManager;

public class WorldModificationListener implements Listener
{
    private Logger          logger;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer  server;

    private boolean canAccess(final Player player, final Location location)
    {
        final WorldManager manager = this.server.<IslandHostManager>getServerManager().getWorldManager(location.getWorld());
        if (manager == null)
        {
            return false;
        }

        final Island island = manager.getIslands().getByChunk(location.getChunk());
        if (island == null || !island.getLocation().isInside(location))
        {
            return false;
        }

        final IslandData islandData = island.getIslandData();
        final UUID       playerId   = player.getUniqueId();

        return islandData.getOwnerId().equals(playerId) || islandData.getMembersUuid().contains(playerId);
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event)
    {
        if (! this.canAccess(event.getPlayer(), event.getBlock().getLocation()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event)
    {
        if (! this.canAccess(event.getPlayer(), event.getBlock().getLocation()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDamage(final BlockDamageEvent event)
    {
        if (! this.canAccess(event.getPlayer(), event.getBlock().getLocation()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event)
    {
        if (event.getClickedBlock() != null && ! this.canAccess(event.getPlayer(), event.getClickedBlock().getLocation()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerArmorStandManipulate(final PlayerArmorStandManipulateEvent event)
    {
        if (! this.canAccess(event.getPlayer(), event.getRightClicked().getLocation()))
        {
            event.setCancelled(true);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
