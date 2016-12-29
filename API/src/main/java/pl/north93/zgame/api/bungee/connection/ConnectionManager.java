package pl.north93.zgame.api.bungee.connection;

import java.util.ArrayList;
import java.util.Set;

import org.diorite.utils.math.DioriteRandomUtils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.server.Server;

/**
 * Klasa ułatwiająca łączenia graczy z konkretnymi serwerami
 * i grupami serwerów.
 */
public class ConnectionManager
{
    private final INetworkManager networkManager = API.getNetworkManager();

    public void connectPlayerToServersGroup(final ProxiedPlayer player, final String serversGroup)
    {
        final Server server = this.getBestServerFromServersGroup(serversGroup);
        if (server == null)
        {
            return;
        }

        player.connect(ProxyServer.getInstance().getServerInfo(server.getProxyName()));
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
}
