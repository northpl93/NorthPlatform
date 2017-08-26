package pl.arieals.minigame.goldhunter.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.BlockVector;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.MiniGameApi;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.goldhunter.GoldHunterArena;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class BlockBreakListener implements AutoListener
{
    @Inject
    private MiniGameServer miniGameServer;
    
    @EventHandler
    public void onBreakChest(BlockBreakEvent event)
    {
        GameHostManager gameHostManager = miniGameServer.getServerManager();
        LocalArena localArena = gameHostManager.getArenaManager().getArena(event.getBlock().getWorld());
        
        if ( localArena == null )
        {
            return;
        }
        
        GoldHunterArena arena = localArena.getArenaData();
        
        BlockVector chestLoc = event.getBlock().getLocation().toVector().toBlockVector();
        if ( arena.getChests().values().contains(chestLoc) )
        {
            arena.breakChest(MiniGameApi.getPlayerData(event.getPlayer(), GoldHunterPlayer.class), chestLoc);
            event.setCancelled(true);
        }
    }
}
