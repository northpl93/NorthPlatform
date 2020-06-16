package pl.north93.northplatform.api.global.network.server;

/**
 * Interfejs zdalnego wywolywania procedur uzywany do komunikacji
 * z konkretnym serwerem minecrafta.
 * Jest zaimplementowany przez komponent API.BukkitServerManager.
 */
public interface IServerRpc
{
    /**
     * @return aktualna ilosc graczy na serwerze.
     */
    int getOnlinePlayers();

    /**
     * @return czy serwer jest zaplanowany do wylaczenia.
     */
    boolean isShutdownScheduled();

    /**
     * Planuje wylaczenie tego serwera.
     * Serwer samodzielnie zaktualizuje swoj obiekt i
     * ustawi wartosc pola shutdown.
     */
    void setShutdownScheduled();

    /**
     * Probuje anulowac wylaczenie serwera.
     * Gdy sie z jakiegos powodu nie uda zwroci false.
     *
     * @return Czy udalo sie anulowac wylaczenie.
     */
    boolean cancelShutdown();
}
