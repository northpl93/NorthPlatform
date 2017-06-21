package pl.arieals.minigame.elytrarace.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameEndEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.ScoreController;
import pl.north93.zgame.api.bukkit.utils.SimpleCountdown;

public class ArenaEndListener implements Listener
{
    @EventHandler
    public void onGameEnd(final GameEndEvent event)
    {
        final LocalArena arena = event.getArena();
        final ElytraRaceArena arenaData = arena.getArenaData();

        arenaData.getMetaHandler().gameEnd(arena);
        // zabijamy wszystkie nasze entities.
        arenaData.getScoreControllers().values().forEach(ScoreController::cleanup);

        new SimpleCountdown(100).endCallback(arena::prepareNewCycle).start();
    }

    // gdy gracz wychodzi z areny informujemy o tym metahandlera
    // zeby ewentualnie zakonczyl gre jesli wszyscy pozostali gracze
    // sa na mecie
    @EventHandler
    private void onPlayerQuit(final PlayerQuitArenaEvent event)
    {
        final LocalArena arena = event.getArena();
        final ElytraRaceArena arenaData = arena.getArenaData();
        arenaData.getMetaHandler().playerQuit(arena, event.getPlayer());
    }
}
