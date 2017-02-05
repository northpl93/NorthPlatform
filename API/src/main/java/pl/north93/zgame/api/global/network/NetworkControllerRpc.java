package pl.north93.zgame.api.global.network;

import java.util.UUID;

import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.api.global.redis.rpc.annotation.DoNotWaitForResponse;

public interface NetworkControllerRpc
{
    /**
     * Nic nie robi.
     */
    void ping(); // default - 1 sec timeout

    /**
     * Wyłącza kontroler sieci.
     */
    @DoNotWaitForResponse
    void stopController();

    /**
     * Przeładowuje uprawnienia w kontrolerze sieci.
     */
    @DoNotWaitForResponse
    void updateConfigs();

    /**
     * Zmienia stan serwera o podanym UUID
     *
     * @param serverId unikalny identyfikator serwera
     * @param serverState nowy stan serwera
     */
    @DoNotWaitForResponse
    void updateServerState(UUID serverId, ServerState serverState);

    /**
     * Usuwa serwer o podanym UUID
     *
     * @param serverId unikalny identyfikator serwera
     */
    @DoNotWaitForResponse
    void removeServer(UUID serverId);
}
