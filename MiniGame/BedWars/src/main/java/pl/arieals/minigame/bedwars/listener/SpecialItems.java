package pl.arieals.minigame.bedwars.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import pl.north93.zgame.api.bukkit.utils.FastBlockOp;

public class SpecialItems implements Listener
{
    @EventHandler
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

    @EventHandler
    public void onBucketUse(final PlayerBucketEmptyEvent event)
    {
        final Player player = event.getPlayer();
        player.getInventory().setItemInMainHand(null);
    }
}
