package pl.north93.zgame.api.chat.global;

import java.util.Collection;

import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.network.players.PlayerNotFoundException;

/**
 * Główny interfejs systemu zarządzania pokojami czatu.
 * Dostępny na wszystkich platformach.
 */
public interface ChatManager
{
    /**
     * Tworzy nowy pokój czatu o podanym ID.
     *
     * @param id Unikalne ID nowo tworzonego pokoju czatu.
     * @param formatter Formatter używany w tym pokoju.
     * @param priority Priorytet tego pokoju podczas wybierania głównego
     *                 pokoju gracza.
     * @return Obiekt reprezentujący nowo utworzony pokój czatu o podanym ID.
     */
    ChatRoom createRoom(String id, ChatFormatter formatter, int priority);

    /**
     * Zwraca instancję już istniejącego pokoju czatu lub tworzy nowy pokój
     * z podanym formatterem.
     *
     * @param id Unikalne ID pokoju czatu.
     * @param formatter Formatter używany w tym pokoju.
     *                  Zostaje ustawiony tylko przy tworzeniu pokoju!
     * @param priority Priorytet tego pokoju podczas wybierania głównego
     *                 pokoju gracza. Zostaje ustawiony tylko przy tworzeniu pokoju!
     * @return Obiekt reprezentujący pokój czatu o podanym ID.
     */
    ChatRoom getOrCreateRoom(String id, ChatFormatter formatter, int priority);

    /**
     * Zwraca instancję już istniejącego pokoju czatu.
     *
     * @param id Unikalne ID pokoju czatu.
     * @return Obiekt reprezentujący pokój czatu o podanym ID.
     */
    ChatRoom getRoom(String id) throws ChatRoomNotFoundException;

    /**
     * Zwraca listę wszystkich pokojów czatu.
     * Należy uważać podczas używania jej, niektóre pokoje mogły już zniknąć
     * i zostanie rzucony {@link ChatRoomNotFoundException}.
     *
     * @return Lista wszystkich pokojów czatu.
     */
    Collection<ChatRoom> getChatRooms();

    Collection<ChatRoom> findRooms(String regex);

    /**
     * Usuwa pokój czatu o podanym identyfikatorze.
     *
     * @param id Unikalne ID pokoju czatu.
     * @throws ChatRoomNotFoundException Jeśli pokój czatu o podanym ID nie zostanie odnaleziony.
     */
    void deleteRoom(String id) throws ChatRoomNotFoundException;

    ChatPlayer getPlayer(Identity identity) throws PlayerNotFoundException;
}
