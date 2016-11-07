package pl.north93.zgame.api.global.network.server;

import java.util.Optional;
import java.util.UUID;

import pl.north93.zgame.api.global.deployment.ServersGroup;
import pl.north93.zgame.api.global.network.JoiningPolicy;

/**
 * Klasa reprezenyująca serwer w sieci.
 */
public interface Server extends ServerProxyData
{
    UUID getUuid();

    ServerType getType();

    boolean isLaunchedViaDaemon();

    ServerState getServerState();

    JoiningPolicy getJoiningPolicy();

    /**
     * Opcjonalnie zwraca grupę serwerów do której należy ten serwer.
     *
     * @return grupa serwerów do której należy ten serwer.
     */
    Optional<ServersGroup> getServersGroup();
}
