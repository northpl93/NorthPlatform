package pl.north93.zgame.api.global.network.server;

import java.util.Set;
import java.util.UUID;

import pl.north93.zgame.api.global.network.impl.ServerDto;
import pl.north93.zgame.api.global.network.server.group.IServersGroup;
import pl.north93.zgame.api.global.network.server.group.ServersGroupDto;
import pl.north93.zgame.api.global.redis.observable.Hash;
import pl.north93.zgame.api.global.redis.observable.Value;

/**
 * Definiuje podstawowe metody sluzace do pobierania informacji
 * o serwerach uruchomionych w sieci.
 */
public interface IServersManager
{
    /**
     * Zwraca instancję Server serwera o podanym UUID.
     * @param uuid identyfikator serwera.
     * @return instancja serwera
     */
    Server withUuid(UUID uuid);

    /**
     * Zwraca interfejs zdalnego wywolywania procedur dla serwera
     * o podanym unikalnym identyfikatorze.
     * @param uuid uuid serwera ktorego zwrocic interfejs RPC.
     * @return interfejs RPC serwera o podanym uuid.
     */
    IServerRpc getServerRpc(UUID uuid);

    default IServerRpc getServerRpc(final Server server)
    {
        return this.getServerRpc(server.getUuid());
    }

    /**
     * Zwraca listę wszystkich serwerów.
     * @return wszystkie serwery.
     */
    Set<? extends Server> all();

    /**
     * Zwraca listę wszystkich serwerów znajdujących się w
     * danej grupie serwerów.
     * @param group grupa serwerów.
     * @return serwery z danej grupy.
     */
    Set<Server> inGroup(String group);

    /**
     * Zwraca listę wszystkich grup serwerów będących aktualnie aktywnych w sieci.
     * @return wszystkie grupy serwerów.
     */
    Set<? extends IServersGroup> getServersGroups();

    /**
     * Zwraca grupę serwerów o podanej nazwie.
     * @param name Nazwa grupy serwerów.
     * @return Grupa serwerów o podanej nazwie.
     */
    IServersGroup getServersGroup(String name);

    Unsafe unsafe();

    interface Unsafe
    {
        Value<ServerDto> getServerDto(final UUID serverId);

        Hash<ServersGroupDto> getServersGroups();
    }
}
