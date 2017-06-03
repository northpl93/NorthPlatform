package pl.arieals.minigame.elytrarace.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class MoveListener implements Listener
{
    @Inject
    private MiniGameServer server;

    @EventHandler
    public void onMove(final PlayerMoveEvent event)
    {
        final LocalArena arena = getArena(event.getPlayer());
        final ElytraRaceArena data = arena.getArenaData();
        if (arena.getGamePhase() == GamePhase.STARTED && ! data.isStarted())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDisableFly(final PlayerToggleFlightEvent event)
    {
        final LocalArena arena = getArena(event.getPlayer());
        final ElytraRaceArena data = arena.getArenaData();
        if (arena.getGamePhase() == GamePhase.STARTED && ! data.isStarted())
        {
            event.setCancelled(true);
        }
    }
}
