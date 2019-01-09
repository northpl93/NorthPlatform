package pl.north93.northplatform.api.bungee.proxy;

public interface IProxyServerManager
{
    /**
     * Zwraca managera lokalnej listy serwerow bungeecorda.
     *
     * @return manager lokalnej listy serwerow bungeecorda.
     */
    IProxyServerList getServerList();
}
