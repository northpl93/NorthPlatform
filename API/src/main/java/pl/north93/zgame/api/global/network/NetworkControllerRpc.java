package pl.north93.zgame.api.global.network;

import java.util.UUID;

import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.api.global.redis.rpc.annotation.DoNotWaitForResponse;

public interface NetworkControllerRpc
{
    /**
     * Nic nie robi.
     */
    void ping();

    /**
     * Zmienia stan serwera o podanym UUID
     *
     * @param serverId unikalny identyfikator serwera
     * @param serverState nowy stan serwera
     */
    @DoNotWaitForResponse
    void updateServerState(UUID serverId, ServerState serverState);
}
