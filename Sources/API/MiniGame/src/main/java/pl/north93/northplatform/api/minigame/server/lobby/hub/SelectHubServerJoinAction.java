package pl.north93.northplatform.api.minigame.server.lobby.hub;

import java.util.Collections;

import org.bukkit.Location;

import lombok.ToString;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.server.joinaction.IServerJoinAction;
import pl.north93.northplatform.api.minigame.server.IServerManager;
import pl.north93.northplatform.api.minigame.server.lobby.LobbyManager;
import pl.north93.serializer.platform.annotations.NorthTransient;

@ToString(of = "hubId")
public class SelectHubServerJoinAction implements IServerJoinAction
{
    @NorthTransient
    @Inject
    private IServerManager serverManager;
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
        final LobbyManager lobbyManager = (LobbyManager) this.serverManager;
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
        this.serverManager.tpToHub(Collections.singleton(player), this.hubId);
    }
}
