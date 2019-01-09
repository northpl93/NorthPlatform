package pl.north93.northplatform.api.chat.global.impl;

import java.util.Locale;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import pl.north93.northplatform.api.chat.global.ChatRoom;
import pl.north93.northplatform.api.chat.global.impl.data.BroadcastMessage;

/*default*/ abstract class AbstractChatRoom implements ChatRoom
{
    protected final ChatManagerImpl chatManager;
    protected final String          id;

    public AbstractChatRoom(final ChatManagerImpl chatManager, final String id)
    {
        this.chatManager = chatManager;
        this.id = id;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public void broadcast(final BaseComponent component)
    {
        final String jsonMessage = ComponentSerializer.toString(component);
        final BroadcastMessage message = new BroadcastMessage(this.getId(), jsonMessage);

        this.chatManager.sendMessage(message);
    }

    @Override
    public void broadcast(final Locale locale, final BaseComponent component)
    {
        final String jsonMessage = ComponentSerializer.toString(component);
        final BroadcastMessage message = new BroadcastMessage(this.getId(), jsonMessage, locale.toLanguageTag());

        this.chatManager.sendMessage(message);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("id", this.id).toString();
    }
}
