package pl.north93.zgame.api.bungee.proxy;

public interface IProxyServerManager
{
    /**
     * Zwraca managera lokalnej listy serwerow bungeecorda.
     *
     * @return manager lokalnej listy serwerow bungeecorda.
     */
    IProxyServerList getServerList();
}
