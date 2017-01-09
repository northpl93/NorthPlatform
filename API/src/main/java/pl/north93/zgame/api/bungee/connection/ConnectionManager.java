package pl.north93.zgame.api.bungee.connection;

import java.util.ArrayList;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.math.DioriteRandomUtils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.impl.Injector;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.joinaction.JoinActionsContainer;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;

/**
 * Klasa ułatwiająca łączenia graczy z konkretnymi serwerami
 * i grupami serwerów.
 */
public class ConnectionManager
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager     networkManager;
    @InjectComponent("API.Database.Redis.Observer")
    private IObservationManager observationManager;

    public ConnectionManager()
    {
        Injector.inject(API.getApiCore().getComponentManager(), this);
    }

    public void connectPlayerToServer(final ProxiedPlayer player, final String serverName, JoinActionsContainer actions)
    {
        if (actions.getServerJoinActions().length != 0)
        {
            final Value<JoinActionsContainer> value = this.observationManager.get(JoinActionsContainer.class, "serveractions:" + player.getName());
            value.set(actions);
            value.expire(10);
        }
        player.connect(ProxyServer.getInstance().getServerInfo(serverName));
    }

    public void connectPlayerToServersGroup(final ProxiedPlayer player, final String serversGroup, JoinActionsContainer actions)
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
        final Set<Server> servers = this.networkManager.getServers(serversGroup);
        if (servers.isEmpty())
        {
            return null;
        }

        return DioriteRandomUtils.getRandom(new ArrayList<>(servers));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
