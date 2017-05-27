package pl.arieals.minigame.bedwars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;

public class ArenaStartListener implements Listener
{
    @EventHandler
    public void onStart(final GameStartEvent event)
    {
        event.getArena().setArenaData(new BedWarsArena());
    }
}
