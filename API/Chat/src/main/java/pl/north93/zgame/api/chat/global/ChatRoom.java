package pl.north93.zgame.api.chat.global;

import java.util.Collection;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.zgame.api.global.network.players.Identity;

public interface ChatRoom
{
    String getId();

    Collection<Identity> getParticipants();

    ChatFormatter getChatFormatter();

    void setChatFormatter(ChatFormatter chatFormatter);

    void broadcast(BaseComponent component);
}
