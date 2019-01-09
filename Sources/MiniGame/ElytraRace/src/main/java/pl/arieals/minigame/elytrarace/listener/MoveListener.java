package pl.arieals.minigame.elytrarace.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;


import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;

public class MoveListener implements Listener
{
    @EventHandler
    public void onMove(final PlayerMoveEvent event)
    {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ())
        {
            return;
        }

        final LocalArena arena = getArena(event.getPlayer());
        if (arena == null)
        {
            // arena moze byc nullem gdy gracz tylko oglada itp.
            return;
        }

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
