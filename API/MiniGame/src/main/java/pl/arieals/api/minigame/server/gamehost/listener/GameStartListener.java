package pl.arieals.api.minigame.server.gamehost.listener;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
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
import pl.arieals.api.minigame.shared.api.match.IMatchAccess;
import pl.arieals.api.minigame.shared.api.match.IMatchManager;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.Identity;

public class GameStartListener implements Listener
{
    @Inject
    private IMatchManager matchManager;

    @EventHandler(priority = EventPriority.LOW) // before normal
    public void onGameStart(final GameStartEvent event)
    {
        final IMatchAccess match = this.createMatch(event.getArena());
        event.getArena().setMatch(match);

        final ArenaStartScheduler startScheduler = event.getArena().getStartScheduler();
        if (startScheduler.isStartScheduled())
        {
            // Gdy uzywamy komendy /gamephase i startujemy gre przed koncem odliczania
            // to musimy anulowac to odliczanie, bo inaczej gra sie zbuguje
            startScheduler.cancelStarting();
        }

        // zaczynamy odliczanie czasu gry w wewnętrznym liczniku areny
        event.getArena().getTimer().start(0, TimeUnit.MILLISECONDS, true);
    }

    // metoda pomocnicza tworząca nowy IMatchAccess na podstawie podanej areny
    private IMatchAccess createMatch(final LocalArena arena)
    {
        final List<Player> players = arena.getPlayersManager().getPlayers();
        final List<Identity> identities = players.stream().map(Identity::of).collect(Collectors.toList());

        return this.matchManager.createMatch(arena.getId(), arena.getMiniGame(), arena.getServerId(), identities);
    }
    
    @EventHandler
    public void onLobbyInit(final LobbyInitEvent event)
    {
        if ( event.getArena().isDynamic() )
        {
            // jeżeli gra jest dynamiczna ustawiamy nazwe swiata na "Lobby"
            event.getArena().getAsRemoteArena().setWorldId("");
            event.getArena().getAsRemoteArena().setWorldDisplayName("Lobby");
        }
        
        //if ( event.getArena().getPlayersManager().isEnoughToStart() )
        //{
            // jeśli na arenie zostali gracze z poprzedniego cyklu i jest ich wystarczająca
            // ilosć to odpalamy odliczanie do startu
        //    event.getArena().getStartScheduler().scheduleStart();
        //}
    }
    
    @EventHandler
    public void onPlayerJoin(final PlayerJoinArenaEvent event)
    {
        final LocalArena arena = event.getArena();
        // jeżeli gra jest dynamiczna to start jest uzależniony od graczy zapisanych do gry a nie bedacych aktualnie na arenia
        if ( arena.getGamePhase() != GamePhase.LOBBY || arena.isDynamic() )
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
        if ( arena.getGamePhase() != GamePhase.LOBBY || arena.isDynamic() )
        {
            return;
        }
        
        if ( !arena.getPlayersManager().isEnoughToStart() && arena.getStartScheduler().isStartScheduled() )
        {
            arena.getStartScheduler().cancelStarting();
        }
    }
}
