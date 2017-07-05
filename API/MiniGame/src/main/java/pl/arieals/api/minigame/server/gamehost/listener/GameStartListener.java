package pl.arieals.api.minigame.server.gamehost.listener;

import java.util.concurrent.TimeUnit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.gamehost.arena.ArenaStartScheduler;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.LobbyInitEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.arieals.api.minigame.shared.api.GamePhase;

public class GameStartListener implements Listener
{
    @EventHandler(priority = EventPriority.LOW) // before normal
    public void onGameStart(final GameStartEvent event)
    {
        final ArenaStartScheduler startScheduler = event.getArena().getStartScheduler();
        if (startScheduler.isStartScheduled())
        {
            // Gdy uzywamy komendy /gamephase i startujemy gre przed koncem odliczania
            // to musimy anulowac to odliczania, bo inaczej gra sie zbuguje
            startScheduler.cancelStarting();
        }

        event.getArena().getTimer().start(0, TimeUnit.MILLISECONDS, true);
    }
    
    @EventHandler
    public void onLobbyInit(final LobbyInitEvent event)
    {
        if ( event.getArena().getPlayersManager().isEnoughToStart() )
        {
            event.getArena().getStartScheduler().scheduleStart();
        }
    }
    
    @EventHandler
    public void onPlayerJoin(final PlayerJoinArenaEvent event)
    {
        final LocalArena arena = event.getArena();
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
        final LocalArena arena = event.getArena();
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
