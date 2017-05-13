package pl.arieals.api.minigame.server.gamehost.listener;

import java.util.concurrent.TimeUnit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameInitEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartedEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.arieals.api.minigame.shared.api.GamePhase;

public class GameStartListener implements Listener
{
    @EventHandler(priority = EventPriority.LOW) // before normal
    public void onGameStart(final GameStartedEvent event)
    {
        event.getArena().getTimer().start(0, TimeUnit.MILLISECONDS, true);
    }
    
    @EventHandler
    public void onGameInit(final GameInitEvent event)
    {
        if ( event.getArena().getPlayersManager().isEnoughToStart() )
        {
            event.getArena().getStartScheduler().scheduleStart();
        }
    }
    
    @EventHandler
    public void onPlayerJoin(final PlayerJoinArenaEvent event)
    {
        LocalArena arena = event.getArena();
        if ( arena.getGamePhase() != GamePhase.LOBBY )
        {
            return;
        }
        
        if ( arena.getPlayersManager().isEnoughToStart() && !arena.getStartScheduler().isStartScheduled() )
        {
            arena.getStartScheduler().scheduleStart();
        }
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitArenaEvent event)
    {
        LocalArena arena = event.getArena();
        if ( arena.getGamePhase() != GamePhase.LOBBY)
        {
            return;
        }
        
        if ( !arena.getPlayersManager().isEnoughToStart() && arena.getStartScheduler().isStartScheduled() )
        {
            arena.getStartScheduler().cancelStarting();
        }
    }
}
