package pl.arieals.minigame.goldhunter.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.GoldHunterArena;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

public class StructureDestroyListener implements AutoListener
{
    private final GoldHunter goldHunter;
    
    public StructureDestroyListener(GoldHunter goldHunter)
    {
        this.goldHunter = goldHunter;
    }
    
    @EventHandler
    public void onDestroyStructure(BlockBreakEvent event)
    {
        GoldHunterArena arena = goldHunter.getArenaForWorld(event.getBlock().getWorld());
        GoldHunterPlayer player = goldHunter.getPlayer(event.getPlayer());
        
        if ( arena == null || player == null )
        {
            return;
        }
        
        if ( arena.getStructureManager().tryDestroyStructure(player, event.getBlock().getLocation()) )
        {
            event.setCancelled(true);
        }
    }
}
