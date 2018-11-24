package pl.north93.northplatform.api.bungee.proxy;

import pl.north93.northplatform.api.global.network.server.Server;

public interface IProxyServerList
{
    void synchronizeServers();

    void addServer(Server proxyData);

    void removeServer(Server proxyData);

    void removeAllServers();
}
