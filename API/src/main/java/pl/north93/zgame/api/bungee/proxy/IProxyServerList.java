package pl.north93.zgame.api.bungee.proxy;

import pl.north93.zgame.api.global.network.server.Server;

public interface IProxyServerList
{
    void synchronizeServers();

    void addServer(Server proxyData);

    void removeServer(Server proxyData);

    void removeAllServers();
}
