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
        if ( event.getArena().getGamePhase() != GamePhase.STARTED )
        {
            // 1. jesli arena jest w lobby to nie ma sensu nic robic
            //    gra po prostu nie wystartuje (zbyt mala ilosc graczy do startu)
            // 2. jesli arena jest w post_game to tez nie trzeba nic robic
            //    bo minigra powinna obsluzyc powrot areny do LOBBY po
            //    jakims czasie
            return;
        }
        
        if (event.getArena().getPlayersManager().getPlayers().size() > 0)
        {
            return;
        }

        event.getArena().setGamePhase(GamePhase.LOBBY);
    }
}
