package pl.north93.zgame.api.chat.global;

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
    String getId();

    int getPriority();

    Collection<Identity> getParticipants();

    ChatFormatter getChatFormatter();

    void setChatFormatter(ChatFormatter chatFormatter);

    void broadcast(BaseComponent component);

    void broadcast(Locale locale, BaseComponent component);

    void delete();
}
