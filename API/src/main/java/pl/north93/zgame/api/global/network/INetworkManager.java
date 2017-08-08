package pl.north93.zgame.api.global.network;

import java.util.Set;

import pl.north93.zgame.api.global.deployment.RemoteDaemon;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
import pl.north93.zgame.api.global.network.proxy.ProxyInstanceInfo;
import pl.north93.zgame.api.global.network.server.IServersManager;
import pl.north93.zgame.api.global.redis.observable.Value;

public interface INetworkManager
{
    Value<NetworkMeta> getNetworkMeta();

    JoiningPolicy getJoiningPolicy();

    /**
     * Zwraca aktualną listę serwerów proxy podłączonych do sieci.
     *
     * @return lista serwerów proxy.
     */
    Set<ProxyInstanceInfo> getProxyServers();

    /**
     * Zwraca aktualną listę demonów podłączonych do sieci.
     *
     * @return lista demonów.
     */
    Set<RemoteDaemon> getDaemons();

    void broadcastNetworkAction(NetworkAction networkAction);

    NetworkControllerRpc getNetworkController();

    IServersManager getServers();

    IPlayersManager getPlayers();
}
