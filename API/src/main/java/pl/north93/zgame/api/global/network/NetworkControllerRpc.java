package pl.north93.zgame.api.global.network;

import java.util.UUID;

import pl.north93.zgame.api.global.network.server.ServerState;

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
    void updateServerState(UUID serverId, ServerState serverState);
}
