package pl.north93.northplatform.api.minigame.server.lobby;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.server.IServersManager;
import pl.north93.northplatform.api.global.network.server.Server;
import pl.north93.northplatform.api.minigame.server.lobby.hub.LocalHubServer;
import pl.north93.northplatform.api.minigame.server.lobby.hub.SelectHubServerJoinAction;
import pl.north93.northplatform.api.minigame.shared.api.hub.IHubServer;
import pl.north93.northplatform.api.minigame.shared.api.hub.RemoteHub;
import pl.north93.northplatform.api.minigame.shared.impl.HubsManager;

public class LobbyHubsManager
{
    @Inject
    private IServersManager serversManager;
    @Inject
    private HubsManager hubsManager;
    private final LocalHubServer localHub;

    @Bean
    private LobbyHubsManager()
    {
        this.localHub = new LocalHubServer();
        this.hubsManager.setHub(new RemoteHub(this.localHub));
        this.localHub.refreshConfiguration();
    }

    public LocalHubServer getLocalHub()
    {
        return this.localHub;
    }

    public Collection<? extends IHubServer> getAllHubServers()
    {
        return this.hubsManager.getAllHubs();
    }

    public void tpToHub(final Iterable<? extends Player> players, final String hubId)
    {
        for (final Player player : players)
        {
            this.localHub.movePlayerToHub(player, hubId);
        }
    }

    public void tpToHub(final Iterable<? extends Player> players, final IHubServer hubServer, final String hubId)
    {
        final UUID targetServerId = hubServer.getServerId();
        if (targetServerId.equals(this.localHub.getServerId()))
        {
            this.tpToHub(players, hubId);
            return;
        }

        final Server server = this.serversManager.withUuid(targetServerId);
        for (final Player player : players)
        {
            final INorthPlayer northPlayer = INorthPlayer.wrap(player);
            northPlayer.connectTo(server, new SelectHubServerJoinAction(hubId));
        }
    }

    public void unregister()
    {
        this.hubsManager.removeHub(this.localHub.getServerId());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("localHub", this.localHub).toString();
    }
}
