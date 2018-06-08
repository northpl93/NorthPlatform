package pl.arieals.api.minigame.server.lobby;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.lobby.hub.LocalHubServer;
import pl.arieals.api.minigame.server.lobby.hub.SelectHubServerJoinAction;
import pl.arieals.api.minigame.shared.api.hub.IHubServer;
import pl.arieals.api.minigame.shared.api.hub.RemoteHub;
import pl.arieals.api.minigame.shared.impl.HubsManager;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.server.Server;

public class LobbyHubsManager
{
    @Inject
    private HubsManager     hubsManager;
    @Inject
    private INetworkManager networkManager;
    private LocalHubServer  localHub;

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

    public void tpToHub(final Collection<Player> players, final String hubId)
    {
        for (final Player player : players)
        {
            this.localHub.movePlayerToHub(player, hubId);
        }
    }

    public void tpToHub(final Collection<Player> players, final IHubServer hubServer, final String hubId)
    {
        final UUID targetServerId = hubServer.getServerId();
        if (targetServerId.equals(this.localHub.getServerId()))
        {
            this.tpToHub(players, hubId);
            return;
        }

        final Server server = this.networkManager.getServers().withUuid(targetServerId);
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
