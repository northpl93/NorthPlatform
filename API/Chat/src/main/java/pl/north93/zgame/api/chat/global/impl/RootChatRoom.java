package pl.north93.zgame.api.chat.global.impl;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;

import pl.north93.zgame.api.chat.global.ChatFormatter;
import pl.north93.zgame.api.chat.global.ChatRoom;
import pl.north93.zgame.api.global.network.players.Identity;

/*default*/ class RootChatRoom extends AbstractChatRoom
{
    public RootChatRoom(final ChatManagerImpl chatManager)
    {
        super(chatManager, "root");
    }

    @Nullable
    @Override
    public ChatRoom getParent()
    {
        return null;
    }

    @Override
    public Collection<ChatRoom> getChildren()
    {
        // zwraca wszystkie pokoje bez rodziców
        return this.chatManager.getChatRooms(data -> data.getParent() == null);
    }

    @Override
    public void addChild(final ChatRoom chatRoom)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeChild(final ChatRoom chatRoom)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isChild(final ChatRoom chatRoom)
    {
        return chatRoom != this;
    }

    @Override
    public int getPriority()
    {
        return 0;
    }

    @Override
    public Collection<Identity> getParticipants()
    {
        return Collections.emptyList();
    }

    @Override
    public ChatFormatter getChatFormatter()
    {
        return null;
    }

    @Override
    public void setChatFormatter(final ChatFormatter chatFormatter)
    {
    }

    @Override
    public void delete()
    {
        throw new UnsupportedOperationException();
    }
}
