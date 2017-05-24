package pl.north93.zgame.api.bungee.mods.impl;

import java.net.InetSocketAddress;
import java.util.Iterator;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.north93.zgame.api.bungee.mods.IBungeeServersManager;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerProxyData;

/**
 * Klasa zarządzająca serwerami dostępnymi w Proxy.
 * Odpowiada za modyfikacje refleksjami tej listy.
 */
public class BungeeServersManager implements IBungeeServersManager
{
    private final ProxyServer proxyServer = ProxyServer.getInstance();

    @Override
    public void synchronizeServers()
    {
        API.getLogger().info("Synchronizing servers...");
        this.removeAllServers();
        for (final Server server : API.getNetworkManager().getServers().all())
        {
            this.addServer(server);
        }
    }

    @Override
    public void addServer(final ServerProxyData proxyData)
    {
        final String name = proxyData.getProxyName();
        final InetSocketAddress address = new InetSocketAddress(proxyData.getConnectHost(), proxyData.getConnectPort());

        final ServerInfo serverInfo = this.proxyServer.constructServerInfo(name, address, name, false);

        this.proxyServer.getConfig().getServers().put(name, serverInfo);
    }

    @Override
    public void removeServer(final String serverName)
    {
        this.proxyServer.getConfig().getServers().get(serverName).getPlayers().forEach(ProxiedPlayer::disconnect);
        this.proxyServer.getConfig().getServers().remove(serverName);
    }

    @Override
    public void removeAllServers()
    {
        final Iterator<ServerInfo> iterator = this.proxyServer.getConfig().getServers().values().iterator();
        while (iterator.hasNext())
        {
            final ServerInfo server = iterator.next();
            server.getPlayers().forEach(ProxiedPlayer::disconnect);
            iterator.remove();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("proxyServer", this.proxyServer).toString();
    }
}
