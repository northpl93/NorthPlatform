package pl.arieals.api.minigame.server.gamehost.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.arieals.api.minigame.shared.api.GamePhase;

public class ArenaEndListener implements Listener
{
    @EventHandler
    public void stopEmptyArena(final PlayerQuitArenaEvent event)
    {
        if (event.getArena().getPlayersManager().getPlayers().size() > 0)
        {
            return;
        }

        event.getArena().setGamePhase(GamePhase.POST_GAME);
    }
}
