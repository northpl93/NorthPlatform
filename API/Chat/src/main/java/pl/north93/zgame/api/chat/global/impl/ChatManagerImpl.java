package pl.north93.zgame.api.chat.global.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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

public class ChatManagerImpl implements ChatManager
{
    @Inject
    private Logger          logger;
    @Inject
    private IEventManager   eventManager;
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
            value.set(new ChatRoomData(priority, formatterId));
        }

        this.logger.log(Level.INFO, "Created room with ID {0} and formatter {1}", new Object[]{id, formatterId});
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
                value.set(new ChatRoomData(priority, formatterId));
                this.logger.log(Level.INFO, "Created room with ID {0} and formatter {1}", new Object[]{id, formatterId});
            }
        }

        return new ChatRoomImpl(this, id, value);
    }

    @Override
    public void deleteRoom(final String id) throws ChatRoomNotFoundException
    {
        // to od razu zweryfikuje czy pokój istnieje
        final ChatRoomImpl room = this.getRoom(id);

        // wyrzuca wszystkich graczy i usuwa pokój z redisa
        room.kickAllAndDelete();
    }

    @Override
    public ChatRoomImpl getRoom(final String id) throws ChatRoomNotFoundException
    {
        final Value<ChatRoomData> value = this.chatRooms.getAsValue(id);
        if (! value.isPreset())
        {
            throw new ChatRoomNotFoundException(id);
        }
        return new ChatRoomImpl(this, id, value);
    }

    @Override
    public ChatPlayerImpl getPlayer(final Identity identity) throws PlayerNotFoundException
    {
        final IPlayersManager manager = this.getPlayersManager();

        final Identity validIdentity = manager.completeIdentity(identity);
        final Value<IOnlinePlayer> playerValue = manager.unsafe().getOnline(validIdentity.getNick());

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