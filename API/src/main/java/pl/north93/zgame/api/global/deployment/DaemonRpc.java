package pl.north93.zgame.api.global.deployment;

import java.util.UUID;

import pl.north93.zgame.api.global.redis.rpc.annotation.DoNotWaitForResponse;

public interface DaemonRpc
{
    @DoNotWaitForResponse
    void setAcceptingNewServers(Boolean isAcceptingNewServers);

    @DoNotWaitForResponse
    void deployServer(UUID serverUuid, String templateName);

    @DoNotWaitForResponse
    void deleteServer(UUID serverUuid);
}
