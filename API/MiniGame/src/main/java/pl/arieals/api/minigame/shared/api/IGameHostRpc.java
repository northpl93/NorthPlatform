package pl.arieals.api.minigame.shared.api;

import java.util.List;
import java.util.UUID;

import pl.arieals.api.minigame.server.shared.api.PlayerJoinInfo;
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
     * Próbuje dodać graczy(a) do areny o podanym ID.
     * Umożliwia także dodawanie obserwatorów gry.
     *
     * @param players lista graczy którzy mają zastać dodani
     * @param arenaId identyfikator areny
     * @param spectator czy dodajemy spectatorów
     * @return czy serwer zezwolił na dołączenie (czy nie przekraczamy ilości graczy itp) i dodał graczy
     */
    Boolean tryConnectPlayers(List<PlayerJoinInfo> players, UUID arenaId, Boolean spectator);
}
