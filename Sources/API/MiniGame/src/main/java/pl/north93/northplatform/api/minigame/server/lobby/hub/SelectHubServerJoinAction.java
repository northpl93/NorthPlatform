package pl.north93.northplatform.api.minigame.server.lobby.hub;

import lombok.ToString;
import org.bukkit.Location;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.server.joinaction.IServerJoinAction;
import pl.north93.northplatform.api.minigame.server.MiniGameServer;
import pl.north93.northplatform.api.minigame.server.lobby.LobbyManager;
import pl.north93.serializer.platform.annotations.NorthTransient;

import java.util.Collections;

@ToString(of = "hubId")
public class SelectHubServerJoinAction implements IServerJoinAction
{
    @NorthTransient
    @Inject
    private MiniGameServer miniGameServer;
    private String hubId;

    public SelectHubServerJoinAction()
    {
    }

    public SelectHubServerJoinAction(final String hubId)
    {
        this.hubId = hubId;
    }

    @Override
    public void playerPreSpawn(final INorthPlayer player, final Location spawn)
    {
        final LobbyManager lobbyManager = this.miniGameServer.getServerManager();
        final HubWorld hubWorld = lobbyManager.getLocalHub().getHubWorld(this.hubId);

        final Location hubSpawn = hubWorld.getSpawn();
        spawn.setWorld(hubSpawn.getWorld());
        spawn.setX(hubSpawn.getX());
        spawn.setY(hubSpawn.getY());
        spawn.setZ(hubSpawn.getZ());
    }

    @Override
    public void playerJoined(final INorthPlayer player)
    {
        final LobbyManager lobbyManager = this.miniGameServer.getServerManager();
        lobbyManager.tpToHub(Collections.singleton(player), this.hubId);
    }
}
