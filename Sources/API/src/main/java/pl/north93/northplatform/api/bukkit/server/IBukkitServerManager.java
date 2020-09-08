package pl.north93.northplatform.api.bukkit.server;

import java.io.File;
import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

import pl.north93.northplatform.api.bukkit.server.event.ShutdownScheduledEvent;
import pl.north93.northplatform.api.global.network.server.Server;
import pl.north93.northplatform.api.global.network.server.ServerState;

/**
 * Deklarancja zestawu metod wystawianych przez komponent API.BukkitServerManager.
 * Zarzadza on publicznym dokumentem serwera {@link Server}, a takze
 * odpowiada za planowanie wylaczenia serwera.
 */
public interface IBukkitServerManager
{
    UUID getServerId();

    /**
     * Zwraca immutable instancje dokumenty reprezentujaca dany serwer.
     *
     * @return niemutowalna instancja dokumentu.
     */
    Server getServer();

    /**
     * Rejestruje podane listenery w Bukkicie.
     * @param listeners listenery do zarejestrowania.
     */
    void registerEvents(Listener... listeners);

    /**
     * Wywołuje dany event a następnie zwraca jego instancję.
     * @param event event do wywołania.
     * @param <T> typ eventu.
     * @return instancja podana jako argument.
     */
    <T extends Event> T callEvent(T event);

    File getServerDirectory();

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
     * @see ShutdownScheduledEvent
     */
    void scheduleShutdown();

    /**
     * Probuje zanulowac wylaczenie serwera.
     * Moze byc niemozliwe gdy serwer sie juz wylacza.
     *
     * @throws IllegalStateException Gdy serwer juz sie wylacza lub gdy wylaczenie nie jest zaplanowane.
     */
    void cancelShutdown();

    FixedMetadataValue createFixedMetadataValue(Object value);
}
