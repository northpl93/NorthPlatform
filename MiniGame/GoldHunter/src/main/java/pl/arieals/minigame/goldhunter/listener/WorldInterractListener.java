package pl.arieals.minigame.goldhunter.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.GoldHunterArena;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

public class WorldInterractListener implements AutoListener
{
    private final GoldHunter goldHunter;
    
    public WorldInterractListener(GoldHunter goldHunter)
    {
        this.goldHunter = goldHunter;
    }
    
    @EventHandler
    public void onBreakBlockOnSpawn(BlockBreakEvent event)
    {
        GoldHunterArena arena = goldHunter.getArenaForWorld(event.getBlock().getWorld());
        GoldHunterPlayer player = goldHunter.getPlayer(event.getPlayer());
        
        if ( player == null )
        {
            return;
        }
        
        if ( !player.isIngame() || arena == null || !arena.canBuild(event.getBlock().getLocation()) )
        {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPhisicsUpdate(BlockPhysicsEvent event)
    {
        GoldHunterArena arena = goldHunter.getArenaForWorld(event.getBlock().getWorld());
        
        if ( arena == null )
        {
            return;
        }
        
        if ( !arena.canBuild(event.getBlock().getLocation())
                || arena.getStructureManager().isStructure(event.getBlock().getLocation()) ) // don't let update state of blocks that are part of structure entity
        {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockChangeByEntity(EntityChangeBlockEvent event)
    {
        GoldHunterArena arena = goldHunter.getArenaForWorld(event.getBlock().getWorld());
        
        if ( arena == null || !arena.canBuild(event.getBlock().getLocation()) )
        {
            event.setCancelled(true);
            // TODO: smoke effect on spawn 
        }
    }
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event)
    {
        GoldHunterArena arena = goldHunter.getArenaForWorld(event.getEntity().getWorld());        
        event.blockList().removeIf(b -> arena == null || !arena.canBuild(b.getLocation()));
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFormEvent(BlockGrowEvent event)
    {
        event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeavesDecay(LeavesDecayEvent event)
    {
        event.setCancelled(true);
    }
}
