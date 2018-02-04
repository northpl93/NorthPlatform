package pl.arieals.minigame.goldhunter.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

public class BlockDropListener implements AutoListener
{
    private final GoldHunter goldHunter;
    
    public BlockDropListener(GoldHunter goldHunter)
    {
        this.goldHunter = goldHunter;
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
