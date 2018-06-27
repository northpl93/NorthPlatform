package pl.arieals.api.minigame.shared.api;

import java.util.List;
import java.util.UUID;

import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.api.arena.reconnect.ReconnectTicket;

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

    /**
     * Próbuje ponownie dodać gracza do areny wskazanej w {@link ReconnectTicket}.
     *
     * @param ticket ReconnectTicket pobrany z sieciowego obiektu gracza.
     * @return czy serwer zezwolił na ponowne dołączenie i dodał gracza.
     */
    Boolean tryReconnect(ReconnectTicket ticket);
}
