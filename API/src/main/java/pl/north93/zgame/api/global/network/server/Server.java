package pl.north93.zgame.api.global.network.server;

import java.util.Optional;
import java.util.UUID;

import pl.north93.zgame.api.global.deployment.ServersGroup;
import pl.north93.zgame.api.global.network.JoiningPolicy;

public interface Server
{
    UUID getUuid();

    ServerType getType();

    boolean isLaunchedViaDaemon();

    ServerState getServerState();

    JoiningPolicy getJoiningPolicy();

    Optional<ServersGroup> getServersGroup();
}
