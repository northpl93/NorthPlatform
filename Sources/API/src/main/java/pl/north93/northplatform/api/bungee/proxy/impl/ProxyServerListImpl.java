package pl.north93.northplatform.api.bungee.proxy.impl;

import static java.util.Collections.synchronizedMap;


import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.north93.northplatform.api.bungee.proxy.IProxyServerList;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.server.IServersManager;
import pl.north93.northplatform.api.global.network.server.Server;

@Slf4j
class ProxyServerListImpl implements IProxyServerList
{
    @Inject
    private IServersManager serversManager;
    private final ProxyServer proxyServer;
    private final Map<String, ServerInfo> servers;

    public ProxyServerListImpl()
    {
        this.proxyServer = ProxyServer.getInstance();
        this.servers = synchronizedMap(this.proxyServer.getConfig().getServers());
    }

    @Override
    public void synchronizeServers()
    {
        log.info("Adding all servers actually existing in network...");
        this.removeAllServers();
        for (final Server server : this.serversManager.all())
        {
            this.addServer(server);
        }
    }

    @Override
    public void addServer(final Server server)
    {
        final String name = server.getProxyName();
        final InetSocketAddress address = new InetSocketAddress(server.getConnectHost(), server.getConnectPort());

        final ServerInfo serverInfo = this.proxyServer.constructServerInfo(name, address, name, false);

        this.servers.put(name, serverInfo);
    }

    @Override
    public void removeServer(final Server server)
    {
        final String proxyName = server.getProxyName();

        final ServerInfo serverInfo = this.servers.get(proxyName);
        if (serverInfo == null)
        {
            // z jakiegos powodu serwer juz nie istnieje, zabezpieczenie przed ewentualnym NPE
            log.warn("Tried to removeServer({}), but server doesnt exist", server.getProxyName());
            return;
        }

        serverInfo.getPlayers().forEach(ProxiedPlayer::disconnect);
        this.servers.remove(proxyName);
    }

    @Override
    public void removeAllServers()
    {
        final Iterator<ServerInfo> iterator = this.servers.values().iterator();
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
