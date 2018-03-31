package pl.north93.zgame.api.chat.global.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.chat.global.ChatFormatter;
import pl.north93.zgame.api.chat.global.ChatManager;
import pl.north93.zgame.api.chat.global.ChatPlayer;
import pl.north93.zgame.api.chat.global.ChatRoom;
import pl.north93.zgame.api.chat.global.ChatRoomNotFoundException;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.network.players.PlayerNotFoundException;
import pl.north93.zgame.api.global.redis.observable.Hash;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;

public class ChatManagerImpl implements ChatManager
{
    @Inject
    private Logger          logger;
    @Inject
    private INetworkManager networkManager;
    private final Map<String, ChatFormatter> formatters;
    private final Hash<ChatRoomData>         chatRooms;

    @Bean
    private ChatManagerImpl(final IObservationManager observationManager)
    {
        this.formatters = new HashMap<>();
        this.chatRooms = observationManager.getHash(ChatRoomData.class, "chatRooms");
    }

    @Aggregator(ChatFormatter.class)
    public void addFormatter(final ChatFormatter formatter)
    {
        final String formatterId = this.getFormatterId(formatter);
        this.formatters.put(formatterId, formatter);
    }

    public String getFormatterId(final ChatFormatter chatFormatter)
    {
        return chatFormatter.getClass().getName();
    }

    public ChatFormatter getFormatter(final String formatterId)
    {
        return this.formatters.get(formatterId);
    }

    public Logger getLogger()
    {
        return this.logger;
    }

    public IPlayersManager getPlayersManager()
    {
        return this.networkManager.getPlayers();
    }

    @Override
    public ChatRoom createRoom(final String id, final ChatFormatter formatter)
    {
        final Value<ChatRoomData> value = this.chatRooms.getAsValue(id);
        if (value.isPreset())
        {
            throw new IllegalArgumentException("Room " + id + " already exist");
        }

        final String formatterId = this.getFormatterId(formatter);
        value.set(new ChatRoomData(formatterId));

        this.logger.log(Level.INFO, "Created room with ID {0} and formatter {1}", new Object[]{id, formatterId});
        return new ChatRoomImpl(this, id, value);
    }

    @Override
    public void deleteRoom(final String id)
    {
        // to od razu zweryfikuje czy pokój istnieje
        final ChatRoomImpl room = this.getRoom(id);

        // wyrzuca wszystkich graczy i usuwa pokój z redisa
        room.kickAllAndDelete();
    }

    @Override
    public ChatRoomImpl getRoom(final String id)
    {
        final Value<ChatRoomData> value = this.chatRooms.getAsValue(id);
        if (! value.isPreset())
        {
            throw new ChatRoomNotFoundException(id);
        }
        return new ChatRoomImpl(this, id, value);
    }

    @Override
    public ChatPlayer getPlayer(final Identity identity) throws PlayerNotFoundException
    {
        final IPlayersManager manager = this.getPlayersManager();

        final Identity validIdentity = manager.completeIdentity(identity);
        final Value<IOnlinePlayer> playerValue = manager.unsafe().getOnline(validIdentity.getNick());

        return new ChatPlayerImpl(this, playerValue);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("formatters", this.formatters).append("chatRooms", this.chatRooms).toString();
    }
}
