package pl.north93.zgame.api.chat.global.impl;

import static java.util.Optional.ofNullable;


import javax.annotation.Nullable;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

    public ChatPlayerImpl(final ChatManagerImpl chatManager, final Value<IOnlinePlayer> player)
    {
        this.chatManager = chatManager;
        this.identity = player.get().getIdentity();
        this.player = player;
    }

    @Override
    public Identity getIdentity()
    {
        return this.identity;
    }

    @Nullable
    @Override
    public ChatRoom getMainRoom()
    {
        final IOnlinePlayer player = this.player.get();

        final ChatPlayerData data = ChatPlayerData.get(player);
        return ofNullable(data.getMainRoomId()).map(this.chatManager::getRoom).orElse(null);
    }

    @Override
    public void setMainRoom(final ChatRoom room)
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

            // w metodzie update wyżej może zostaćzucony wyjątek o nieistniejącym pokoju
            final ChatPlayerData playerData = ChatPlayerData.get(player);
            playerData.getRooms().add(room.getId());

            final Logger logger = this.chatManager.getLogger();
            logger.log(Level.INFO, "Player {0} joined chat room {1}", new Object[]{player.getLatestNick(), room.getId()});

            this.checkIsMainRoomNeeded(playerData);
        }
    }

    @Override
    public void leaveRoom(final ChatRoom room)
    {
        final ChatRoomImpl roomImpl = (ChatRoomImpl) room;
        try (final IPlayerTransaction t = this.chatManager.getPlayersManager().transaction(this.identity))
        {
            final IPlayer player = t.getPlayer();
            if (t.isOffline())
            {
                throw new PlayerOfflineException(player);
            }

        }
    }

    private void checkIsMainRoomNeeded(final ChatPlayerData data)
    {
        if (data.getMainRoomId() != null)
        {
            return;
        }

        data.getRooms().stream().findFirst().ifPresent(data::setMainRoomId);
    }
}
