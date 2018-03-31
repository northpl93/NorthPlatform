package pl.north93.zgame.api.chat.global;

import java.util.Collection;

import pl.north93.zgame.api.global.network.players.Identity;

public interface ChatRoom
{
    String getId();

    Collection<Identity> getParticipants();

    ChatFormatter getChatFormatter();

    void setChatFormatter(ChatFormatter chatFormatter);
}
