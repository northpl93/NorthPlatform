package pl.north93.zgame.api.chat.global;

import javax.annotation.Nullable;

import java.util.Collection;

import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.network.players.PlayerOfflineException;

public interface ChatPlayer
{
    Identity getIdentity();

    boolean isOnline();

    Collection<ChatRoom> getChatRooms() throws PlayerOfflineException;

    boolean isInRoom(ChatRoom room) throws PlayerOfflineException;

    void joinRoom(ChatRoom room) throws PlayerOfflineException;

    void leaveRoom(ChatRoom room) throws PlayerOfflineException;

    @Nullable
    ChatRoom getActiveRoom() throws PlayerOfflineException;

    void setPreferredMainRoom(ChatRoom room) throws PlayerOfflineException;
}
