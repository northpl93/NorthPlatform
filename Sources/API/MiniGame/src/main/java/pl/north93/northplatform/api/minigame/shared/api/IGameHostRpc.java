package pl.north93.northplatform.api.minigame.shared.api;

import java.util.List;
import java.util.UUID;

import pl.north93.northplatform.api.minigame.shared.api.arena.RemoteArena;
import pl.north93.northplatform.api.minigame.shared.api.arena.reconnect.ReconnectTicket;
import pl.north93.northplatform.api.global.metadata.MetaStore;

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
     * Jeśli się uda, metadata areny zostanie zaktualizowana.
     *
     * @param players lista graczy którzy mają zastać dodani
     * @param arenaId identyfikator aren.
     * @param metadata Matadata która zostanie ustawiona po poprawnym dodaniu graczy.
     * @return czy serwer zezwolił na dołączenie (czy nie przekraczamy ilości graczy itp) i dodał graczy
     */
    boolean tryConnectPlayers(List<PlayerJoinInfo> players, UUID arenaId, MetaStore metadata);

    /**
     * Próbuje dodać graczy(a) do areny o podanym ID jako spectator.
     *
     * @param players lista graczy którzy mają zastać dodani
     * @param arenaId identyfikator aren.
     * @return czy serwer zezwolił na dołączenie (czy nie przekraczamy ilości graczy itp) i dodał graczy
     */
    boolean tryConnectSpectators(List<PlayerJoinInfo> players, UUID arenaId);

    /**
     * Próbuje ponownie dodać gracza do areny wskazanej w {@link ReconnectTicket}.
     *
     * @param ticket ReconnectTicket pobrany z sieciowego obiektu gracza.
     * @return czy serwer zezwolił na ponowne dołączenie i dodał gracza.
     */
    boolean tryReconnect(ReconnectTicket ticket);
}
