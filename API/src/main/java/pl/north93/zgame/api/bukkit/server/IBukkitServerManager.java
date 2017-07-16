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
     * @return niemutowalna instancja dokumentu.
     */
    Server getServer();

    /**
     * Atomowo zmienia stan przechowywany wewnatrz dokumentu serwera.
     * <p>
     * UWAGA! Ta metoda nie wywoluje innych czynnosci, poza ustawieniem
     * wartosci pola
     * @param newState nowy stan serwera w dokumencie.
     */
    void changeState(ServerState newState);

    boolean isShutdownScheduled();

    void scheduleShutdown();

    void cancelShutdown();
}
