package pl.north93.zgame.api.global.network;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import pl.north93.zgame.api.global.deployment.RemoteDaemon;
import pl.north93.zgame.api.global.deployment.ServerPattern;
import pl.north93.zgame.api.global.deployment.serversgroup.IServersGroup;
import pl.north93.zgame.api.global.messages.NetworkMeta;
import pl.north93.zgame.api.global.messages.ProxyInstanceInfo;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.redis.observable.Value;

public interface INetworkManager extends IPlayerManagement
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

    Set<Server> getServers();

    Set<Server> getServers(String serversGroup);

    Set<IServersGroup> getServersGroups();

    IServersGroup getServersGroup(String name);

    List<ServerPattern> getServerPatterns();

    ServerPattern getServerPattern(String name);

    Value<Server> getServer(UUID uuid);

    void broadcastNetworkAction(NetworkAction networkAction);

    NetworkControllerRpc getNetworkController();

    IPlayersManager getPlayers();
}
