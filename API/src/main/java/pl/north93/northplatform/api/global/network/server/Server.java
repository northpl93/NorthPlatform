package pl.north93.northplatform.api.global.network.server;

import java.util.UUID;

import pl.north93.northplatform.api.global.network.server.group.IServersGroup;
import pl.north93.northplatform.api.global.redis.rpc.IRpcTarget;
import pl.north93.northplatform.api.global.network.JoiningPolicy;

/**
 * Klasa reprezenyująca serwer w sieci.
 */
public interface Server
{
    /**
     * Zwraca unikalny identyfikator tego serwera.
     *
     * @return UUID tego serwera.
     */
    UUID getUuid();

    /**
     * Metoda zwraca nazwę serwera używaną przez BungeeCorda.
     *
     * @return nazwa serwera w BungeeCordzie.
     */
    default String getProxyName()
    {
        return this.getUuid().toString();
    }

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
     * Zwraca ilość graczy będących online na tym serwerze.
     * Ta wartość aktualizowana jest co jakiś czas, więc na należy na niej zbytnio polegać.
     *
     * @return Ilość graczy na tym serwerze.
     */
    int getPlayersCount();

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
     * Zwraca grupe serwerow do ktorej nalezy ten serwer.
     * Nie ma mozliwosci uruchomienia serwera bez skonfigurowanej grupy.
     *
     * @return grupa serwerów do której należy ten serwer.
     */
    IServersGroup getServersGroup();

    /**
     * Zwraca nazwę domenową/ip potrzebną do połączenia się z tym serwerem.
     *
     * @return host do połączenia się z tym serwerem.
     */
    String getConnectHost();

    /**
     * Metoda zwracająca port na którym pracuje serwer.
     *
     * @return port na którym pracuje serwer.
     */
    int getConnectPort();

    /**
     * Zwraca obiekt IRpcTarget do wykorzystania w zdalnym wywoływaniu
     * procedur.
     *
     * @return {@link IRpcTarget}
     */
    IRpcTarget getRpcTarget();
}
