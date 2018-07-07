package pl.north93.zgame.api.chat.global;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.Locale;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.zgame.api.global.network.players.Identity;

/**
 * Reprezentuje pokój czatu.
 * <p>
 * Ten obiekt nie jest immutable; wszystkie zmiany przychodzące z sieci są
 * natychmiast widoczne. Może zostać także rzucony wyjątek {@link ChatRoomNotFoundException},
 * jeśli ktoś inny usunie pokój.
 */
public interface ChatRoom
{
    /**
     * Zwraca unikalny identyfikator kanału reprezentowanego przez ten obiekt.
     * Unikalne identyfikatory kanałów są łańcuchami tekstowymi nadawanymi przez użytkownika API.
     * Ta metoda nigdy nie zwróci null i nigdy nie rzuci wyjątku {@link ChatRoomNotFoundException}.
     *
     * @return unikalny identyfikator kanału.
     */
    String getId();

    /**
     * Zwraca rodzica tego pokoju lub {@literal null}, jeśli nie ma rodzica.
     *
     * @throws ChatRoomNotFoundException Jeśli pokój reprezentowany przez tą instancję został usunięty.
     * @return rodzic tego pokoju lub null.
     */
    @Nullable
    ChatRoom getParent();

    /**
     * Zwraca kolekcję dzieci tego pokoju czatu.
     *
     * @throws ChatRoomNotFoundException Jeśli pokój reprezentowany przez tą instancję został usunięty.
     * @return kolekcja dzieci tego pokoju czatu.
     */
    Collection<ChatRoom> getChildren();

    /**
     * Dodaje nowe dziecko do tego pokoju czatu.
     * Dodawany pokój nie może być już dzieckiem innego pokoju.
     *
     * @throws IllegalStateException Jeśli dodawany pokój jest już dzieckiem.
     * @throws ChatRoomNotFoundException Jeśli pokój reprezentowany przez tą instancję lub
     *                                   pokój w argumencie został usunięty.
     * @param chatRoom Pokój który dodajemy jako dziecko.
     */
    void addChild(ChatRoom chatRoom);

    /**
     * Usuwa dziecko tego kanału czatu.
     *
     * @throws IllegalStateException Jeśli usuwany pokój nie jest dzieckiem tego pokoju.
     * @throws ChatRoomNotFoundException Jeśli pokój reprezentowany przez tą instancję lub
     *                                   pokój w argumencie został usunięty.
     * @param chatRoom Pokój który usuwamy z listy dzieci tego kanału.
     */
    void removeChild(ChatRoom chatRoom);

    /**
     * Sprawdza czy pokój podany w argumencie jest dzieckiem tego pokoju.
     *
     * @throws ChatRoomNotFoundException Jeśli pokój reprezentowany przez tą instancję został usunięty.
     * @param chatRoom Pokój który sprawdzamy.
     * @return True jeśli pokój z argumentu jest dzieckiem tego pokoju.
     */
    boolean isChild(ChatRoom chatRoom);

    /**
     * Zwraca priorytet tego pokoju.
     * Gracz będąc w kilku pokojach wysyła wiadomości na ten z najwyższym priorytetem.
     *
     * @throws ChatRoomNotFoundException Jeśli pokój reprezentowany przez tą instancję został usunięty.
     * @return Priorytet tego pokoju.
     */
    int getPriority();

    /**
     * Zwraca listę użytkowników będących aktualnie w tym pokoju.
     *
     * @throws ChatRoomNotFoundException Jeśli pokój reprezentowany przez tą instancję został usunięty.
     * @return Lista użytkowników będących w tym pokoju.
     */
    Collection<Identity> getParticipants();

    /**
     * Zwraca obiekt odpowiedzialny za formatowanie czatu w tym pokoju.
     *
     * @throws ChatRoomNotFoundException Jeśli pokój reprezentowany przez tą instancję został usunięty.
     * @return Obiekt odpowiedzialny za formatowanie czatu w tym pokoju.
     */
    ChatFormatter getChatFormatter();

    /**
     * Ustawia obiekt odpowiedzialny za formatowanie czatu w tym pokoju.
     * Klasa obiektu musi być dostępna na bungeecordzie!
     *
     * @throws ChatRoomNotFoundException Jeśli pokój reprezentowany przez tą instancję został usunięty.
     * @param chatFormatter Nowy obiekt formatujący czat.
     */
    void setChatFormatter(ChatFormatter chatFormatter);

    /**
     * Rozgłasza wiadomość do wszystkich użytkowników w pokoju, bez
     * zawracania uwagi na język.
     *
     * @param component Wiadomość do rozgłoszenia.
     */
    void broadcast(BaseComponent component);

    /**
     * Rozgłasza wiadomość do użytkowników w pokoju korzystających z podanego języka.
     *
     * @param locale Język wiadomości, wiadomość otrzymają tylko gracze z ustawionym tym językiem.
     * @param component Wiadomość do rozgłoszenia.
     */
    void broadcast(Locale locale, BaseComponent component);

    void delete();
}
