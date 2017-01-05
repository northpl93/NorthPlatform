package pl.north93.zgame.api.global.deployment.serversgroup;

import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.server.ServerType;

public interface IServersGroup
{
    /**
     * Zwraca rodzaj tej grupy serwerów.
     *
     * @return rodzaj grupy serwerów.
     */
    ServersGroupType getType();

    String getName();

    ServerType getServersType();

    JoiningPolicy getJoiningPolicy();
}
