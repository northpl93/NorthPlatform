package pl.arieals.api.minigame.server.gamehost.listener;

import java.util.concurrent.TimeUnit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.gamehost.event.arena.GameStartedEvent;

public class GameStartListener implements Listener
{
    @EventHandler(priority = EventPriority.LOW) // before normal
    public void onGameStart(final GameStartedEvent event)
    {
        event.getArena().getTimer().start(0, TimeUnit.MILLISECONDS, true);
    }
}
