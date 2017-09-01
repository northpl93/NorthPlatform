package pl.north93.zgame.api.global.network.server;

import java.util.Optional;
import java.util.UUID;

import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.server.group.IServersGroup;
import pl.north93.zgame.api.global.redis.rpc.IRpcTarget;

/**
 * Klasa reprezenyująca serwer w sieci.
 */
public interface Server extends ServerProxyData
{
    /**
     * Zwraca unikalny identyfikator tego serwera.
     *
     * @return UUID tego serwera.
     */
    UUID getUuid();

    /**
     * Zwraca typ tego serwera.
     * LOBBY lub MINIGAME.
     *
     * @return typ serwera.
     */
    ServerType getType();

    /**
     * Sprawdza czy ten serwer został uruchomiony przez demona.
     * W przypadku środowiska produkcyjnego to zawsze będzie prawdą.
     *
     * @return true jeśli serwer został uruchomiony przez demona.
     */
    boolean isLaunchedViaDaemon();

    /**
     * Zwraca aktualny stan serwera.
     *
     * @return aktualny stan serwera.
     */
    ServerState getServerState();

    /**
     * Sprawdza czy ten serwer jest zaplanowany do wylaczenia.
     *
     * @return czy serwer zaplanowany do wylaczenia.
     */
    boolean isShutdownScheduled();

    /**
     * Zwraca aktualną politykę wchodzenia na serwer.
     *
     * @return aktualna polityka wchodzenia na serwer.
     */
    JoiningPolicy getJoiningPolicy();

    /**
     * Opcjonalnie zwraca grupę serwerów do której należy ten serwer.
     *
     * @return grupa serwerów do której należy ten serwer.
     */
    Optional<IServersGroup> getServersGroup();

    /**
     * Zwraca obiekt IRpcTarget do wykorzystania w zdalnym wywoływaniu
     * procedur.
     *
     * @return {@link IRpcTarget}
     */
    IRpcTarget getRpcTarget();
}
