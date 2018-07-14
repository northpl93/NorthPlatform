package pl.north93.zgame.api.chat.global.impl;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pl.north93.zgame.api.chat.global.ChatPlayer;
import pl.north93.zgame.api.chat.global.ChatRoom;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.network.players.PlayerOfflineException;
import pl.north93.zgame.api.global.redis.observable.Value;

/*default*/ class ChatPlayerImpl implements ChatPlayer
{
    private final ChatManagerImpl      chatManager;
    private final Identity             identity;
    private final Value<IOnlinePlayer> player;

    public ChatPlayerImpl(final ChatManagerImpl chatManager, final Identity identity, final Value<IOnlinePlayer> player)
    {
        this.chatManager = chatManager;
        this.identity = identity;
        this.player = player;
    }

    @Override
    public Identity getIdentity()
    {
        return this.identity;
    }

    @Override
    public boolean isOnline()
    {
        // gdy obiekt znajduje sie w redisie to gracz musi byc online
        return this.player.isPreset();
    }

    @Nullable
    @Override
    public ChatRoom getActiveRoom()
    {
        final IOnlinePlayer player = this.player.get();

        final ChatPlayerData data = ChatPlayerData.get(player);
        if (data.getMainRoomId() == null)
        {
            return this.getBestMainRoom(data);
        }

        return this.chatManager.getRoom(data.getMainRoomId());
    }

    @Override
    public void setPreferredMainRoom(final ChatRoom room)
    {
        try (final IPlayerTransaction t = this.chatManager.getPlayersManager().transaction(this.identity))
        {
            final ChatPlayerData playerData = ChatPlayerData.get(t.getPlayer());
            playerData.setMainRoomId(room.getId());
        }
    }

    @Override
    public Collection<ChatRoom> getChatRooms()
    {
        final IOnlinePlayer player = this.player.get();

        final ChatPlayerData data = ChatPlayerData.get(player);
        return data.getRooms().stream().map(this.chatManager::getRoom).collect(Collectors.toList());
    }

    @Override
    public boolean isInRoom(final ChatRoom room)
    {
        final IOnlinePlayer player = this.player.get();

        final ChatPlayerData data = ChatPlayerData.get(player);
        return data.getRooms().contains(room.getId());
    }

    @Override
    public void joinRoom(final ChatRoom room)
    {
        final Logger logger = this.chatManager.getLogger();
        final ChatRoomImpl roomImpl = (ChatRoomImpl) room;

        try (final IPlayerTransaction t = this.chatManager.getPlayersManager().transaction(this.identity))
        {
            final IPlayer player = t.getPlayer();
            if (t.isOffline())
            {
                throw new PlayerOfflineException(player);
            }

            // aktualizujemy pokój i dodajemy do niego gracza
            roomImpl.update(data -> data.getParticipants().add(player.getIdentity()));

            // w metodzie update wyżej może zostać rzucony wyjątek o nieistniejącym pokoju
            final ChatPlayerData playerData = ChatPlayerData.get(player);
            if (! playerData.getRooms().add(room.getId()))
            {
                return;
            }

            logger.log(Level.INFO, "Player {0} joined chat room {1}", new Object[]{player.getLatestNick(), room.getId()});
        }
    }

    @Override
    public void leaveRoom(final ChatRoom room, final boolean ignoreOffline)
    {
        final Logger logger = this.chatManager.getLogger();
        final ChatRoomImpl roomImpl = (ChatRoomImpl) room;

        try (final IPlayerTransaction t = this.chatManager.getPlayersManager().transaction(this.identity))
        {
            final IPlayer player = t.getPlayer();
            if (! ignoreOffline && t.isOffline())
            {
                throw new PlayerOfflineException(player);
            }

            roomImpl.update(data -> data.getParticipants().remove(player.getIdentity()));

            // w metodzie update wyżej może zostać rzucony wyjątek o nieistniejącym pokoju
            final ChatPlayerData playerData = ChatPlayerData.get(player);
            if (! playerData.getRooms().remove(room.getId()))
            {
                return;
            }

            logger.log(Level.INFO, "Player {0} leaved chat room {1}", new Object[]{player.getLatestNick(), room.getId()});
            if (roomImpl.getId().equals(playerData.getMainRoomId()))
            {
                playerData.setMainRoomId(null);
            }
        }
    }

    // usuwa tego gracza z wszystkich pokojów, ale nie modyfikuje danych samego gracza
    public void leaveAllRoomsUnsafe()
    {
        try (final IPlayerTransaction t = this.chatManager.getPlayersManager().transaction(this.identity))
        {
            final ChatPlayerData playerData = ChatPlayerData.get(t.getPlayer());

            for (final String roomId : playerData.getRooms())
            {
                final ChatRoomImpl room = this.chatManager.getRoom0(roomId);
                room.update(roomData -> roomData.getParticipants().remove(this.identity));
            }
        }
    }

    private ChatRoom getBestMainRoom(final ChatPlayerData player)
    {
        final Stream<ChatRoomImpl> playerRooms = player.getRooms().stream().map(this.chatManager::getRoom0);
        return playerRooms.max(Comparator.comparing(ChatRoom::getPriority)).orElse(null);
    }
}
