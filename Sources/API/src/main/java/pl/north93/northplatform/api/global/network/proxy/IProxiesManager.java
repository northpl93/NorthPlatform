package pl.north93.northplatform.api.global.network.proxy;

import java.util.Set;

import pl.north93.northplatform.api.global.network.server.Server;

/**
 * Menadzer serwerow proxy w sieci.
 */
public interface IProxiesManager
{
    /**
     * Zwraca ilosc graczy bedacych online na wszystkich serwerach proxy.
     * Moze byc niedokladna, zalezy od szybkosci raportowania danych przez serwery proxy.
     *
     * @return ilosc graczy online w sieci.
     */
    int onlinePlayersCount();

    /**
     * Zwraca aktualną listę serwerów proxy podłączonych do sieci.
     *
     * @return lista serwerów proxy.
     */
    Set<ProxyDto> all();

    IProxyRpc getRpc(ProxyDto proxyDto);

    void addOrUpdateProxy(String proxyId, ProxyDto proxyDto);

    void removeProxy(String proxyId);

    /**
     * Dodaje podany serwer do wszystkich polaczonych proxy.
     *
     * @param proxyData Serwer do dodania.
     */
    void addServer(Server proxyData);

    /**
     * Usuwa podany serwer z wszystkich polaczonych proxy.
     *
     * @param proxyData Serwer do usuniecia.
     */
    void removeServer(Server proxyData);
}
