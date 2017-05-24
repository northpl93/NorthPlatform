package pl.north93.zgame.api.global.network.server;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import pl.north93.zgame.api.global.deployment.ServerPattern;
import pl.north93.zgame.api.global.deployment.serversgroup.IServersGroup;

public interface IServersManager
{
    /**
     * Zwraca instancję Server serwera o podanym UUID.
     * @param uuid identyfikator serwera.
     * @return instancja serwera
     */
    Server withUuid(UUID uuid);

    IServerRpc getServerRpc(UUID uuid);

    default IServerRpc getServerRpc(final Server server)
    {
        return this.getServerRpc(server.getUuid());
    }

    /**
     * Zwraca listę wszystkich serwerów.
     * @return wszystkie serwery.
     */
    Set<Server> all();

    /**
     * Zwraca listę wszystkich serwerów znajdujących się w
     * danej grupie serwerów.
     * @param group grupa serwerów.
     * @return serwery z danej grupy.
     */
    Set<Server> inGroup(String group);

    Set<IServersGroup> getServersGroups();

    IServersGroup getServersGroup(String name);

    List<ServerPattern> getServerPatterns();

    ServerPattern getServerPattern(String name);
}
