package pl.north93.zgame.api.global.network.proxy;

import java.util.Set;

import pl.north93.zgame.api.global.network.server.ServerProxyData;
import pl.north93.zgame.api.global.redis.observable.Hash;

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

    /**
     * Dodaje podany serwer do wszystkich polaczonych proxy.
     *
     * @param proxyData Serwer do dodania.
     */
    void addServer(ServerProxyData proxyData);

    /**
     * Usuwa podany serwer z wszystkich polaczonych proxy.
     *
     * @param proxyData Serwer do usuniecia.
     */
    void removeServer(ServerProxyData proxyData);

    Unsafe unsafe();

    interface Unsafe
    {
        Hash<ProxyDto> getHash();
    }
}
