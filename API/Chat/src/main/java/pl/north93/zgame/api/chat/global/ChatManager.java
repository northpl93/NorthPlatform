package pl.north93.zgame.api.chat.global;

import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.network.players.PlayerNotFoundException;

public interface ChatManager
{
    ChatRoom createRoom(String id, ChatFormatter formatter);

    void deleteRoom(String id);

    ChatRoom getRoom(String id);

    ChatPlayer getPlayer(Identity identity) throws PlayerNotFoundException;
}
