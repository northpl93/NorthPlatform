package pl.arieals.api.minigame.server.lobby.hub;

import java.util.Collections;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.server.joinaction.IServerJoinAction;
import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackIgnore;

public class SelectHubServerJoinAction implements IServerJoinAction
{
    @MsgPackIgnore
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
    public void playerJoined(final Player bukkitPlayer)
    {
        final LobbyManager lobbyManager = this.miniGameServer.getServerManager();
        lobbyManager.tpToHub(Collections.singleton(bukkitPlayer), this.hubId);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("hubId", this.hubId).toString();
    }
}
