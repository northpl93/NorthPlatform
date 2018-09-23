package pl.north93.zgame.api.chat.global.impl;

import static java.text.MessageFormat.format;
import static java.util.Collections.unmodifiableCollection;


import javax.annotation.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.zgame.api.chat.global.ChatFormatter;
import pl.north93.zgame.api.chat.global.ChatPlayer;
import pl.north93.zgame.api.chat.global.ChatRoom;
import pl.north93.zgame.api.chat.global.ChatRoomNotFoundException;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.redis.observable.Value;

@Slf4j
/*default*/ class ChatRoomImpl extends AbstractChatRoom
{
    private final Value<ChatRoomData> data;

    public ChatRoomImpl(final ChatManagerImpl chatManager, final String id, final Value<ChatRoomData> data)
    {
        super(chatManager, id);
        this.data = data;
    }

    @Nullable
    @Override
    public ChatRoom getParent()
    {
        final ChatRoomData roomData = this.data.get();
        this.checkIsPresent(roomData);

        return Optional.ofNullable(roomData.getParent())
                       .map(this.chatManager::getRoom)
                       .orElse(null);
    }

    @Override
    public Collection<ChatRoom> getChildren()
    {
        final ChatRoomData roomData = this.data.get();
        this.checkIsPresent(roomData);

        return roomData.getChildren().stream()
                       .map(this.chatManager::getRoom)
                       .collect(Collectors.toList());
    }

    @Override
    public void addChild(final ChatRoom chatRoom)
    {
        this.update(parentData ->
        {
            final ChatRoomImpl childImpl = (ChatRoomImpl) chatRoom;
            childImpl.update(childData ->
            {
                if (childData.getParent() != null)
                {
                    throw new IllegalStateException(format("Channel {0} already has parent {1}", childData.getId(), childData.getParent()));
                }

                childData.setParent(this.id);
                parentData.getChildren().add(childData.getId());
            });
        });
    }

    @Override
    public void removeChild(final ChatRoom chatRoom)
    {
        this.update(parentData ->
        {
            final ChatRoomImpl childImpl = (ChatRoomImpl) chatRoom;
            childImpl.update(childData ->
            {
                if (! this.id.equals(childData.getParent()))
                {
                    throw new IllegalStateException(format("Channel {0} is not child of {1}", childData.getId(), this.id));
                }

                childData.setParent(null);
                parentData.getChildren().remove(childData.getId());
            });
        });
    }

    @Override
    public boolean isChild(final ChatRoom chatRoom)
    {
        final ChatRoomData roomData = this.data.get();
        this.checkIsPresent(roomData);

        return roomData.getChildren().contains(chatRoom.getId());
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
        final String formatterId = this.chatManager.getFormatterId(chatFormatter);

        this.update(roomData ->
        {
            roomData.setFormatterId(formatterId);
            log.info("Changed formatter of {} to {}", this.id, formatterId);
        });
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
                log.warn("Failed to kick user from room", e);
            }
        });

        // usuwamy z tego kanalu wszystkie dzieci, moze troche nieoptymalnie
        this.getChildren().forEach(this::removeChild);

        // teoretycznie istnieje niebezpieczeństwo że ktoś dołączy do pokoju po usunięciu graczy,
        // ale na razie można to zignorować - i tak chyba nikt nie doda graczy do pokoju który umyślnie usuwa?
        this.data.delete();
        log.info("Deleted room with ID {}", this.id);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("data", this.data).toString();
    }
}
