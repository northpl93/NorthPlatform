package pl.north93.zgame.api.global.network.server;

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
    Integer getOnlinePlayers();

    /**
     * @return czy serwer jest zaplanowany do wylaczenia.
     */
    Boolean isShutdownScheduled();

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
    Boolean cancelShutdown();
}
