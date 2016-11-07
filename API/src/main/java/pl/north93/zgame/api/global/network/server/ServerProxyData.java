package pl.north93.zgame.api.global.network.server;

/**
 * Klasa przechowująca informację potrzebne do połączenia się z serwerem.
 */
public interface ServerProxyData
{
    /**
     * Metoda zwraca nazwę serwera używaną przez BungeeCorda.
     *
     * @return nazwa serwera w BungeeCordzie.
     */
    String getProxyName();

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
}
