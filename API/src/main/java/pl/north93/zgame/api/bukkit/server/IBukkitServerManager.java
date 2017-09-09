package pl.north93.zgame.api.bukkit.server;

import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerState;

/**
 * Deklarancja zestawu metod wystawianych przez komponent API.BukkitServerManager.
 * Zarzadza on publicznym dokumentem serwera {@link Server}, a takze
 * odpowiada za planowanie wylaczenia serwera.
 */
public interface IBukkitServerManager
{
    /**
     * Zwraca immutable instancje dokumenty reprezentujaca dany serwer.
     *
     * @return niemutowalna instancja dokumentu.
     */
    Server getServer();

    /**
     * Atomowo zmienia stan przechowywany wewnatrz dokumentu serwera.
     * <p>
     * UWAGA! Ta metoda nie wywoluje innych czynnosci, poza ustawieniem
     * wartosci pola
     *
     * @param newState nowy stan serwera w dokumencie.
     */
    void changeState(ServerState newState);

    /**
     * Sprawdza czy serwer ciagle pracuje (tzn glowna petla jest wykonywana).
     *
     * @return czy serwer ciagle pracuje.
     */
    boolean isWorking();

    /**
     * Sprawdza czy wylaczenie zostalo zaplanowane.
     *
     * @return czy wylaczenie zostalo zaplanowane.
     */
    boolean isShutdownScheduled();

    /**
     * Planuje wylaczenie serwera.
     *
     * @see pl.north93.zgame.api.bukkit.server.event.ShutdownScheduledEvent
     */
    void scheduleShutdown();

    /**
     * Probuje zanulowac wylaczenie serwera.
     * Moze byc niemozliwe gdy serwer sie juz wylacza.
     *
     * @throws IllegalStateException Gdy serwer juz sie wylacza lub gdy wylaczenie nie jest zaplanowane.
     */
    void cancelShutdown();
}
