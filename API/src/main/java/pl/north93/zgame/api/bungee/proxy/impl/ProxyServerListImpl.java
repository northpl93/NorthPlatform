package pl.north93.zgame.api.bungee.proxy.impl;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.north93.zgame.api.bungee.proxy.IProxyServerList;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerProxyData;

public class ProxyServerListImpl implements IProxyServerList
{
    private final ProxyServer proxyServer = ProxyServer.getInstance();
    @Inject
    private Logger          logger;
    @Inject
    private INetworkManager networkManager;

    @Override
    public void synchronizeServers()
    {
        this.logger.info("Adding all servers actually existing in network...");
        this.removeAllServers();
        for (final Server server : this.networkManager.getServers().all())
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
    public void removeServer(final ServerProxyData proxyData)
    {
        final Map<String, ServerInfo> servers = this.proxyServer.getConfig().getServers();
        final String proxyName = proxyData.getProxyName();

        servers.get(proxyName).getPlayers().forEach(ProxiedPlayer::disconnect);
        servers.remove(proxyName);
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
