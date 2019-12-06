package pl.arieals.minigame.elytrarace.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.gamephase.GameEndEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.ScoreController;
import pl.north93.northplatform.api.bukkit.utils.SimpleCountdown;

public class ArenaEndListener implements Listener
{
    /**
     * Czas przez jaki gra bedzie oczekiwala po zakonczeniu areny.
     */
    private static final int POST_GAME_COOLDOWN = 200;

    @EventHandler
    public void onGameEnd(final GameEndEvent event)
    {
        final LocalArena arena = event.getArena();
        final ElytraRaceArena arenaData = arena.getArenaData();

        arenaData.getMetaHandler().gameEnd(arena);
        // zabijamy wszystkie nasze entities.
        arenaData.getScoreControllers().values().forEach(ScoreController::cleanup);

        final SimpleCountdown postGameCountdown = new SimpleCountdown(POST_GAME_COOLDOWN).endCallback(arena::prepareNewCycle);
        arena.getScheduler().runSimpleCountdown(postGameCountdown);
    }

    // gdy gracz wychodzi z areny informujemy o tym metahandlera
    // zeby ewentualnie zakonczyl gre jesli wszyscy pozostali gracze
    // sa na mecie
    @EventHandler
    private void onPlayerQuit(final PlayerQuitArenaEvent event)
    {
        final LocalArena arena = event.getArena();
        if (arena.getGamePhase() != GamePhase.STARTED)
        {
            // nic nie robimy jesli gra sie juz zakonczyla.
            // Moze to spowodowac powrot areny z trybu INITIALISING do POST_GAME i zbugowac API.
            return;
        }

        final ElytraRaceArena arenaData = arena.getArenaData();
        arenaData.getMetaHandler().playerQuit(arena, event.getPlayer());
    }
}
