package pl.arieals.minigame.bedwars.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
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
}
