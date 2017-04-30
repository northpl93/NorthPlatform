package pl.arieals.api.minigame.server.gamehost.lobby.external;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.shared.api.LobbyMode;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;

public class LobbyExitTracker implements Listener
{
    @InjectComponent("MiniGameApi.Server")
    private MiniGameServer server;

    @EventHandler
    public void onPlayerExitLobby(final PlayerTeleportEvent event)
    {
        final GameHostManager gameHostManager = this.server.getServerManager();
        if (gameHostManager.getMiniGame().getLobbyMode() == LobbyMode.INTEGRATED)
        {
            return;
        }

    }
}
