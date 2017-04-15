package pl.arieals.api.minigame.shared.api;

import java.util.List;
import java.util.UUID;

import pl.arieals.api.minigame.shared.api.arena.RemoteArena;

/**
 * Interfejs zdalnego wywoływania procedur do gadania z serwerem hostującym areny.
 */
public interface IGameHostRpc
{
    /**
     * Zwraca listę aren uruchomionych na tym serwerze.
     *
     * @return lista aren z tego serwera.
     */
    List<RemoteArena> getArenas();

    /**
     * Próbuje dodać gracza o podanym identyfikatorze do areny o
     * podanym identyfikatorze.
     *
     * @param playerId UUID gracza.
     * @param arenaId UUID areny.
     * @return czy udało się dodać gracza (czy było wystarczająco miejsca)
     */
    Boolean tryConnectPlayer(UUID playerId, UUID arenaId);
}
