package pl.north93.northplatform.minigame.goldhunter.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import pl.north93.northplatform.minigame.goldhunter.GoldHunter;
import pl.north93.northplatform.minigame.goldhunter.arena.GoldHunterArena;
import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.bukkit.server.AutoListener;

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
