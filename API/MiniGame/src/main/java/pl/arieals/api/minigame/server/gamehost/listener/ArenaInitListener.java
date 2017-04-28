package pl.arieals.api.minigame.server.gamehost.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.GameInitEvent;
import pl.arieals.api.minigame.shared.api.LobbyMode;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;

public class ArenaInitListener implements Listener
{
    @InjectComponent("MiniGameApi.Server")
    private MiniGameServer server;

    @EventHandler(priority = EventPriority.LOW) // before normal
    public void onArenaInit(final GameInitEvent event)
    {
        final GameHostManager hostManager = this.server.getServerManager();
        final LocalArena arena = event.getArena();

        if (hostManager.getMiniGame().isDynamic())
        {
            // todo move all players to game lobby
        }
        else
        {
            // todo kick all players to server lobby
        }

        if (hostManager.getMiniGame().getLobbyMode() == LobbyMode.INTEGRATED)
        {
            // todo load random map
        }
    }
}
