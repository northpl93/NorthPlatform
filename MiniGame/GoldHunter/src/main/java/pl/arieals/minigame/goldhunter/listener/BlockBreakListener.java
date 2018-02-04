package pl.arieals.minigame.goldhunter.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;

import pl.arieals.api.minigame.server.gamehost.MiniGameApi;
import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.GoldHunterArena;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class BlockBreakListener implements AutoListener
{
    @Inject
    private GoldHunter goldHunter;
    
    @EventHandler
    public void onBreakChest(BlockBreakEvent event)
    {
        GoldHunterArena arena = goldHunter.getArenaForWorld(event.getBlock().getWorld());
        if ( arena == null )
        {
            return;
        }
        
        BlockVector chestLoc = event.getBlock().getLocation().toVector().toBlockVector();
        if ( arena.getChests().values().contains(chestLoc) )
        {
            arena.breakChest(MiniGameApi.getPlayerData(event.getPlayer(), GoldHunterPlayer.class), chestLoc);
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockDrop(BlockBreakEvent event)
    {
        GoldHunterPlayer player = goldHunter.getPlayer(event.getPlayer());
        
        if ( player != null )
        {
            event.setExpToDrop(0);
            event.setDropItems(false);
            
            if ( event.getBlock().getType().isSolid() )
            {
                event.getBlock().getWorld().dropItem(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.WOOD));
            }
        }
    }
    
    
}
