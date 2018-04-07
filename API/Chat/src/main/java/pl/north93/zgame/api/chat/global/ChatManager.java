package pl.north93.zgame.api.chat.global;

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
     * @return Obiekt reprezentujący nowo utworzony pokój czatu o podanym ID.
     */
    ChatRoom createRoom(String id, ChatFormatter formatter);

    /**
     * Zwraca instancję już istniejącego pokoju czatu lub tworzy nowy pokój
     * z podanym formatterem.
     *
     * @param id Unikalne ID pokoju czatu.
     * @param formatter Formatter używany w tym pokoju.
     *                  Zostaje ustawiony tylko przy tworzeniu pokoju!
     * @return Obiekt reprezentujący pokój czatu o podanym ID.
     */
    ChatRoom getOrCreateRoom(String id, ChatFormatter formatter);

    /**
     * Zwraca instancję już istniejącego pokoju czatu.
     *
     * @param id Unikalne ID pokoju czatu.
     * @return Obiekt reprezentujący pokój czatu o podanym ID.
     */
    ChatRoom getRoom(String id) throws ChatRoomNotFoundException;

    void deleteRoom(String id) throws ChatRoomNotFoundException;

    ChatPlayer getPlayer(Identity identity) throws PlayerNotFoundException;
}
