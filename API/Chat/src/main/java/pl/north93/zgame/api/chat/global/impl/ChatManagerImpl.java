package pl.north93.zgame.api.chat.global.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.zgame.api.chat.global.ChatFormatter;
import pl.north93.zgame.api.chat.global.ChatManager;
import pl.north93.zgame.api.chat.global.ChatRoom;
import pl.north93.zgame.api.chat.global.ChatRoomNotFoundException;
import pl.north93.zgame.api.chat.global.impl.data.AbstractChatData;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.network.players.PlayerNotFoundException;
import pl.north93.zgame.api.global.redis.event.IEventManager;
import pl.north93.zgame.api.global.redis.observable.Hash;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Lock;
import pl.north93.zgame.api.global.redis.observable.Value;

@Slf4j
public class ChatManagerImpl implements ChatManager
{
    @Inject
    private IEventManager   eventManager;
    @Inject
    private INetworkManager networkManager;
    private final RootChatRoom               rootChatRoom;
    private final Map<String, ChatFormatter> formatters;
    private final Hash<ChatRoomData>         chatRooms;

    @Bean
    private ChatManagerImpl(final IObservationManager observationManager)
    {
        this.rootChatRoom = new RootChatRoom(this);
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

    public IPlayersManager getPlayersManager()
    {
        return this.networkManager.getPlayers();
    }

    @Override
    public RootChatRoom getRootRoom()
    {
        return this.rootChatRoom;
    }

    @Override
    public ChatRoom createRoom(final String id, final ChatFormatter formatter, final int priority)
    {
        final String formatterId = this.getFormatterId(formatter);

        final Value<ChatRoomData> value = this.chatRooms.getAsValue(id);
        try (final Lock lock = value.lock())
        {
            if (value.isPreset())
            {
                // jesli w redisie istnieje juz taki obiekt to blokujemy tworzenie.
                throw new IllegalArgumentException("Room " + id + " already exist");
            }

            // tworzymy nowy obiekt redisie tworząc tym samym kanał
            value.set(new ChatRoomData(id, priority, formatterId));
        }

        log.info("Created room with ID {} and formatter {}", id, formatterId);
        return new ChatRoomImpl(this, id, value);
    }

    @Override
    public ChatRoom getOrCreateRoom(final String id, final ChatFormatter formatter, final int priority)
    {
        final String formatterId = this.getFormatterId(formatter);

        final Value<ChatRoomData> value = this.chatRooms.getAsValue(id);
        try (final Lock lock = value.lock())
        {
            if (! value.isPreset())
            {
                value.set(new ChatRoomData(id, priority, formatterId));
                log.info("Created room with ID {} and formatter {}", id, formatterId);
            }
        }

        return new ChatRoomImpl(this, id, value);
    }

    @Override
    public void deleteRoom(final String id) throws ChatRoomNotFoundException
    {
        // to od razu zweryfikuje czy pokój istnieje
        final ChatRoomImpl room = this.getRoom0(id);

        // wyrzuca wszystkich graczy i usuwa pokój z redisa
        room.kickAllAndDelete();
    }

    @Override
    public ChatRoom getRoom(final String id) throws ChatRoomNotFoundException
    {
        if (this.rootChatRoom.id.equals(id))
        {
            return this.rootChatRoom;
        }

        return this.getRoom0(id);
    }

    public ChatRoomImpl getRoom0(final String id) throws ChatRoomNotFoundException
    {
        final Value<ChatRoomData> value = this.chatRooms.getAsValue(id);
        if (! value.isPreset())
        {
            throw new ChatRoomNotFoundException(id);
        }
        return new ChatRoomImpl(this, id, value);
    }

    @Override
    public Collection<ChatRoom> getChatRooms()
    {
        return this.getChatRooms(chatRoomData -> true);
    }

    public Collection<ChatRoom> getChatRooms(final Predicate<ChatRoomData> filter)
    {
        return this.chatRooms.values().stream().filter(filter).map(data ->
        {
            final String roomId = data.getId();

            final Value<ChatRoomData> value = this.chatRooms.getAsValue(roomId);
            return new ChatRoomImpl(this, roomId, value);
        }).collect(Collectors.toList());
    }

    @Override
    public Collection<ChatRoom> findRooms(final String regex)
    {
        final Pattern pattern = Pattern.compile(regex);

        final List<ChatRoom> rooms = new ArrayList<>();
        for (final ChatRoomData data : this.chatRooms.values())
        {
            final String roomId = data.getId();
            if (! pattern.matcher(roomId).matches())
            {
                continue;
            }

            final Value<ChatRoomData> value = this.chatRooms.getAsValue(roomId);
            rooms.add(new ChatRoomImpl(this, roomId, value));
        }

        return rooms;
    }

    @Override
    public ChatPlayerImpl getPlayer(final Identity identity) throws PlayerNotFoundException
    {
        final IPlayersManager manager = this.getPlayersManager();

        final Identity validIdentity = manager.completeIdentity(identity);
        final Value<IOnlinePlayer> playerValue = manager.unsafe().getOnlineValue(validIdentity.getNick());

        return new ChatPlayerImpl(this, validIdentity, playerValue);
    }

    /**
     * Wysyla do sieci obiekt przechowujacy wiadomosc.
     * Zostanie on obsluzony w czesci Bungee api czatu.
     *
     * @param data Obiekt wiadomosci.
     */
    public void sendMessage(final AbstractChatData data)
    {
        this.eventManager.callEvent(data);
    }

    /**
     * Specjalna metoda używana w implementacji API Czatu usuwająca gracza
     * z wszystkich jego pokojów bez modyfikacji danych samego gracza i bez
     * dodatkowych sprawdzeń.
     * Używane przy wychodzeniu gracza z sieci.
     *
     * @param identity Identity które usuwamy z wszystkich pokojów.
     */
    public void leaveAllRoomsUnsafe(final Identity identity)
    {
        final ChatPlayerImpl chatPlayer = this.getPlayer(identity);
        chatPlayer.leaveAllRoomsUnsafe();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("formatters", this.formatters).append("chatRooms", this.chatRooms).toString();
    }
}
