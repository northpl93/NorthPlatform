package pl.arieals.minigame.bedwars.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;
import static pl.arieals.minigame.bedwars.utils.TeamArmorUtils.createColorArmor;
import static pl.north93.zgame.api.global.utils.JavaUtils.instanceOf;


import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.SpawnEgg;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.npc.BedWarsSkeleton;
import pl.arieals.minigame.bedwars.utils.TeamArmorUtils;
import pl.north93.zgame.api.bukkit.utils.FastBlockOp;

public class SpecialItems implements Listener
{
    @EventHandler(ignoreCancelled = true)
    public void onTntPlace(final BlockPlaceEvent event)
    {
        final Block block = event.getBlock();
        if (block.getType() != Material.TNT)
        {
            return;
        }

        FastBlockOp.setType(block, Material.AIR, (byte) 0);
        block.getWorld().spawn(block.getLocation(), TNTPrimed.class);
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplode(final EntityExplodeEvent event)
    {
        final LocalArena arena = getArena(event.getEntity().getWorld());
        if (arena == null)
        {
            return;
        }
        final BedWarsArena arenaData = arena.getArenaData();

        event.blockList().removeIf(block -> ! arenaData.getPlayerBlocks().remove(block));
    }

    @EventHandler
    public void onExplodeDestroyHanging(final HangingBreakByEntityEvent event)
    {
        if (event.getRemover() instanceof TNTPrimed)
        {
            // gdy tnt wybucha to moze zniszczyc ramki
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketUse(final PlayerBucketEmptyEvent event)
    {
        final Player player = event.getPlayer();
        player.getInventory().remove(event.getItemStack());
        //player.getInventory().setItemInMainHand(null);
    }

    @EventHandler()
    public void throwFireball(final PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();
        final PlayerInventory inventory = player.getInventory();

        final ItemStack itemInMainHand = inventory.getItemInMainHand();
        if (itemInMainHand.getType() != Material.FIREBALL)
        {
            return;
        }

        final int newAmount = itemInMainHand.getAmount() - 1;
        if (newAmount <= 0)
        {
            inventory.setItemInMainHand(null);
        }
        else
        {
            itemInMainHand.setAmount(newAmount);
        }

        player.launchProjectile(Fireball.class, player.getVelocity());
    }

    @EventHandler
    public void applyDamageByFishingRod(final PlayerFishEvent event)
    {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY)
        {
            final Player player = instanceOf(event.getCaught(), Player.class);

            player.damage(2, event.getPlayer());
        }
    }

    // = = = SKELETON STUFF = = =

    @EventHandler
    public void spawnSkeleton(final PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();
        final PlayerInventory inventory = player.getInventory();

        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null)
        {
            return;
        }

        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
        if (playerData == null)
        {
            return;
        }

        final ItemStack itemInMainHand = inventory.getItemInMainHand();
        if (itemInMainHand.getType() != Material.MONSTER_EGG || ((SpawnEgg) itemInMainHand.getData()).getSpawnedType() == EntityType.SKELETON)
        {
            return;
        }

        event.setCancelled(true);
        final int newAmount = itemInMainHand.getAmount() - 1;
        if (newAmount <= 0)
        {
            inventory.setItemInMainHand(null);
        }
        else
        {
            itemInMainHand.setAmount(newAmount);
        }

        final Skeleton skeleton = BedWarsSkeleton.create(clickedBlock.getLocation().add(0, 1, 0), playerData.getTeam());
        skeleton.setMaxHealth(30);
        skeleton.setHealth(30);

        final Color color = TeamArmorUtils.chatColorToColor(playerData.getTeam().getColor());
        final EntityEquipment equipment = skeleton.getEquipment();
        equipment.setHelmet(createColorArmor(Material.LEATHER_HELMET, color));
        equipment.setChestplate(createColorArmor(Material.LEATHER_CHESTPLATE, color));
        equipment.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
        equipment.setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
    }

    @EventHandler
    public void disableSkeletonFire(final EntityCombustEvent event)
    {
        final CraftEntity craftEntity = (CraftEntity) event.getEntity();
        if (craftEntity.getHandle() instanceof BedWarsSkeleton)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void disableSkeletonLoot(final EntityDeathEvent event)
    {
        final CraftEntity craftEntity = (CraftEntity) event.getEntity();
        if (craftEntity.getHandle() instanceof BedWarsSkeleton)
        {
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void disableAllySkeletonDamage(final EntityDamageByEntityEvent event)
    {
        final Player player = instanceOf(event.getDamager(), Player.class);
        final BedWarsSkeleton skeleton = instanceOf(((CraftEntity) event.getEntity()).getHandle(), BedWarsSkeleton.class);

        if (player == null || skeleton == null)
        {
            return;
        }

        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
        if (playerData == null)
        {
            return;
        }

        if (skeleton.getOwner() == playerData.getTeam())
        {
            event.setCancelled(true);
        }
    }
}
