package pl.north93.zgame.api.bungee.connection;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.api.global.network.server.joinaction.JoinActionsContainer;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;

/**
 * Klasa ułatwiająca łączenia graczy z konkretnymi serwerami
 * i grupami serwerów.
 */
public class ConnectionManager
{
    @Inject
    private INetworkManager     networkManager;
    @Inject
    private IObservationManager observationManager;

    public void connectPlayerToServer(final ProxiedPlayer player, final String serverName, final JoinActionsContainer actions)
    {
        if (! actions.isEmpty())
        {
            final Value<JoinActionsContainer> value = this.observationManager.get(JoinActionsContainer.class, "serveractions:" + player.getName());
            value.setExpire(actions, 10, TimeUnit.SECONDS);
        }
        player.connect(ProxyServer.getInstance().getServerInfo(serverName));
    }

    public void connectPlayerToServersGroup(final ProxiedPlayer player, final String serversGroup, final JoinActionsContainer actions)
    {
        final Server server = this.getBestServerFromServersGroup(serversGroup);
        if (server == null)
        {
            return;
        }

        this.connectPlayerToServer(player, server.getProxyName(), actions);
    }

    public Server getBestServerFromServersGroup(final String serversGroup)
    {
        final Predicate<Server> requireWorking = server -> server.getServerState() == ServerState.WORKING;
        final Comparator<Server> playersComparator = Comparator.comparing(Server::getPlayersCount);

        final Set<Server> servers = this.networkManager.getServers().inGroup(serversGroup);
        return servers.stream()
                      .filter(requireWorking)
                      .min(playersComparator)
                      .orElse(null);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
