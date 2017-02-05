package pl.north93.zgame.skyblock.server.listeners.islandhost;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.projectiles.ProjectileSource;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.skyblock.api.IslandRole;
import pl.north93.zgame.skyblock.api.player.SkyPlayer;
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

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event)
    {
        final Island island = this.server.getServerManager().getIslandAt(event.getBlock().getLocation());
        if (! this.server.canAccess(event.getPlayer(), island))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event)
    {
        final Island island = this.server.getServerManager().getIslandAt(event.getBlock().getLocation());
        if (! this.server.canAccess(event.getPlayer(), island))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDamage(final BlockDamageEvent event)
    {
        final Island island = this.server.getServerManager().getIslandAt(event.getBlock().getLocation());
        if (! this.server.canAccess(event.getPlayer(), island))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreakByEntity(final HangingBreakByEntityEvent event)
    {
        final Entity remover = event.getRemover();
        if (remover instanceof Player)
        {
            final Player player = (Player) remover;
            final Island island = this.server.getServerManager().getIslandAt(event.getEntity().getLocation());
            if (! this.server.canAccess(player, island))
            {
                event.setCancelled(true);
            }
        }
        else
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();
        if (event.getClickedBlock() != null)
        {
            if (event.getClickedBlock().getType().equals(Material.MOB_SPAWNER) && !player.hasPermission("skyblock.spawner.change"))
            {
                event.setCancelled(true);
                return;
            }
            final Island island = this.server.getServerManager().getIslandAt(event.getClickedBlock().getLocation());
            if (! this.server.canAccess(player, island))
            {
                event.setCancelled(true);
                return;
            }
        }

        /*final Island island = this.server.getServerManager().getIslandAt(player.getLocation());
        if (! this.server.canAccess(player, island))
        {
            event.setCancelled(true);
        }*/
    }

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event)
    {
        if(event.getCause().equals(PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT) || event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL))
        {
            final Island island = this.server.getServerManager().getIslandAt(event.getFrom());
            if(! this.server.canAccess(event.getPlayer(), island))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerShearEntity(final PlayerShearEntityEvent event)
    {
        final Island island = this.server.getServerManager().getIslandAt(event.getEntity().getLocation());
        if(! this.server.canAccess(event.getPlayer(), island))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketFill(final PlayerBucketFillEvent event)
    {
        final Island island = this.server.getServerManager().getIslandAt(event.getBlockClicked().getLocation());
        if(! this.server.canAccess(event.getPlayer(), island))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event)
    {
        final Island island = this.server.getServerManager().getIslandAt(event.getBlockClicked().getLocation());
        if(! this.server.canAccess(event.getPlayer(), island))
        {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event)
    {
        final Island island = this.server.getServerManager().getIslandAt(event.getRightClicked().getLocation());
        if (! this.server.canAccess(event.getPlayer(), island))
        {
            event.setCancelled(true);
        }
    }

    /*@EventHandler
    public void onPlayerInteractAtEntity(final PlayerInteractAtEntityEvent event)
    {
        if (! this.server.canAccess(event.getPlayer(), event.getRightClicked().getLocation()))
        {
            event.setCancelled(true);
        }
    }*/

    @EventHandler
    public void onPlayerArmorStandManipulate(final PlayerArmorStandManipulateEvent event)
    {
        final Island island = this.server.getServerManager().getIslandAt(event.getRightClicked().getLocation());
        if (! this.server.canAccess(event.getPlayer(), island))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleDestroy(final VehicleDestroyEvent event)
    {
        final Entity destroyer = event.getAttacker();
        if(destroyer instanceof Player)
        {
            final Island island = this.server.getServerManager().getIslandAt(event.getVehicle().getLocation());
            if(! this.server.canAccess((Player) destroyer, island))
            {
                event.setCancelled(true);
            }
        }
        else
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

    @EventHandler
    public void onEntityHit(final EntityDamageByEntityEvent event)
    {
        final Entity damager = event.getDamager();
        if (damager instanceof Player)
        {
            final Island island = this.server.getServerManager().getIslandAt(event.getEntity().getLocation());
            if (! this.server.canAccess((Player) damager, island))
            {
                event.setCancelled(true);
            }
        }
        else if (damager instanceof Projectile)
        {
            final Projectile projectile = (Projectile) damager;
            final ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Player)
            {
                final Island island = this.server.getServerManager().getIslandAt(event.getEntity().getLocation());
                if (! this.server.canAccess((Player) shooter, island))
                {
                    event.setCancelled(true);
                }
            }
            else if (event.getEntityType().equals(EntityType.ARMOR_STAND) || event.getEntityType().equals(EntityType.ITEM_FRAME))
            {
                event.setCancelled(true);
            }
        }
        else if (event.getEntityType().equals(EntityType.ARMOR_STAND) && event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockIgnite(final BlockIgniteEvent event)
    {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING)
        {
            event.setCancelled(true);
        }
    }

    //allow island owners to delete someone's ChestShop
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChestShopBreak(final BlockBreakEvent event)
    {
        final Material type = event.getBlock().getType();
        if (! type.equals(Material.WALL_SIGN) && ! type.equals(Material.SIGN_POST))
        {
            return;
        }

        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(event.getPlayer().getUniqueId()))
        {
            final SkyPlayer skyPlayer = SkyPlayer.get(t.getPlayer());
            if(skyPlayer.getIslandRole().equals(IslandRole.OWNER)) {
                final Island island = this.server.getServerManager().getIslandAt(event.getPlayer().getLocation());
                if (island != null && island.getId().equals(skyPlayer.getIslandId()))
                {
                    event.setCancelled(false);

                }
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
