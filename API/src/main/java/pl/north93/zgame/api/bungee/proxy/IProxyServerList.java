package pl.north93.zgame.api.bungee.proxy;

import pl.north93.zgame.api.global.network.server.ServerProxyData;

public interface IProxyServerList
{
    void synchronizeServers();

    void addServer(ServerProxyData proxyData);

    void removeServer(ServerProxyData proxyData);

    void removeAllServers();
}
