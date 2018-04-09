package pl.north93.zgame.api.chat.global.impl;

import static java.util.Collections.unmodifiableCollection;


import java.util.Collection;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.zgame.api.chat.global.ChatFormatter;
import pl.north93.zgame.api.chat.global.ChatPlayer;
import pl.north93.zgame.api.chat.global.ChatRoom;
import pl.north93.zgame.api.chat.global.ChatRoomNotFoundException;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.redis.observable.Value;

/*default*/ class ChatRoomImpl implements ChatRoom
{
    private final ChatManagerImpl     chatManager;
    private final String              id;
    private final Value<ChatRoomData> data;

    public ChatRoomImpl(final ChatManagerImpl chatManager, final String id, final Value<ChatRoomData> data)
    {
        this.chatManager = chatManager;
        this.id = id;
        this.data = data;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public int getPriority()
    {
        final ChatRoomData roomData = this.data.get();
        this.checkIsPresent(roomData);

        return roomData.getPriority();
    }

    @Override
    public Collection<Identity> getParticipants()
    {
        final ChatRoomData roomData = this.data.get();
        this.checkIsPresent(roomData);

        return unmodifiableCollection(roomData.getParticipants());
    }

    @Override
    public ChatFormatter getChatFormatter()
    {
        final ChatRoomData roomData = this.data.get();
        this.checkIsPresent(roomData);

        final String formatterId = roomData.getFormatterId();
        return this.chatManager.getFormatter(formatterId);
    }

    @Override
    public void setChatFormatter(final ChatFormatter chatFormatter)
    {
        final Logger logger = this.chatManager.getLogger();
        final String formatterId = this.chatManager.getFormatterId(chatFormatter);

        this.data.update(roomData ->
        {
            this.checkIsPresent(roomData);
            roomData.setFormatterId(formatterId);

            logger.log(Level.INFO, "Changed formatter of {0} to {1}", new Object[]{this.id, formatterId});
        });
    }

    @Override
    public void broadcast(final BaseComponent component)
    {
        // todo
    }

    @Override
    public void delete()
    {
        this.kickAllAndDelete();
    }

    public void update(final Consumer<ChatRoomData> updater)
    {
        this.data.update(data ->
        {
            this.checkIsPresent(data);
            updater.accept(data);
        });
    }

    // robi null-check, i throwuje ChatRoomNotFoundException
    private void checkIsPresent(final ChatRoomData data)
    {
        if (data == null)
        {
            throw new ChatRoomNotFoundException(this.id);
        }
    }

    // usuwa wartość z bazy danych i nic więcej nie robi.
    /*default*/ void kickAllAndDelete()
    {
        final Logger logger = this.chatManager.getLogger();
        this.getParticipants().forEach(participant ->
        {
            try
            {
                final ChatPlayer chatPlayer = this.chatManager.getPlayer(participant);
                chatPlayer.leaveRoom(this);
            }
            catch (final Exception e)
            {
                // logujemy informacje, ale kontynuujemy
                logger.log(Level.WARNING, "Failed to kick user from room", e);
            }
        });

        // teoretycznie istnieje niebezpieczeństwo że ktoś dołączy do pokoju po usunięciu graczy,
        // ale na razie można to zignorować - i tak chyba nikt nie doda graczy do pokoju który umyślnie usuwa?
        this.data.delete();
        logger.log(Level.INFO, "Deleted room with ID {0}", this.id);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("id", this.id).append("data", this.data).toString();
    }
}
