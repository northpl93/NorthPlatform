package pl.north93.zgame.skyblock.server.listeners.islandhost;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.management.IslandHostManager;
import pl.north93.zgame.skyblock.server.world.Island;
import pl.north93.zgame.skyblock.server.world.WorldManager;

public class WorldModificationListener implements Listener
{
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
        return ! (island == null || ! island.getLocation().isInside(location)) && island.canBuild(player.getUniqueId());
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

    @EventHandler
    public void onPistonMove(final BlockPistonExtendEvent event)
    {
        final Block block = event.getBlock().getRelative(event.getDirection(), event.getLength() + 1);

        final WorldManager manager = this.server.<IslandHostManager>getServerManager().getWorldManager(block.getWorld());
        if (manager == null)
        {
            return;
        }

        final Island island = manager.getIslands().getByChunk(block.getChunk());
        if (island == null || !island.getLocation().isInside(block.getLocation()))
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
