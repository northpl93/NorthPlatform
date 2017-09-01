package pl.north93.zgame.api.global.network.server.group;

import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.server.ServerType;

/**
 * Interfejs reprezentujacy grupe serwerow.
 */
public interface IServersGroup
{
    /**
     * Zwraca unikalna nazwe tej grupy serwerow.
     *
     * @return unikalna nazwa tej grupy serwerow.
     */
    String getName();

    /**
     * Zwraca rodzaj tej grupy serwerów.
     *
     * @return rodzaj grupy serwerów.
     */
    ServersGroupType getType();

    ServerType getServersType();

    JoiningPolicy getJoiningPolicy();
}
