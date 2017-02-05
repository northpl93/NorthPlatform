package pl.north93.zgame.api.global.network.impl;

import static pl.north93.zgame.api.global.redis.RedisKeys.PLAYERS;


import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.data.players.IPlayersData;
import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;
import pl.north93.zgame.api.global.messages.ProxyInstanceInfo;
import pl.north93.zgame.api.global.network.players.IOfflinePlayer;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Lock;
import pl.north93.zgame.api.global.redis.observable.Value;

class PlayersManagerImpl implements IPlayersManager
{
    private final NetworkManager      networkManager;
    private final IPlayersData        playersData;
    private final IObservationManager observer;
    private final Unsafe              unsafe;

    public PlayersManagerImpl(final NetworkManager networkManager, final IPlayersData playersData, final IObservationManager observer)
    {
        this.networkManager = networkManager;
        this.playersData = playersData;
        this.observer = observer;
        this.unsafe = new PlayersManagerUnsafeImpl();
    }

    @Override
    public int onlinePlayersCount()
    {
        return this.networkManager.getProxyServers().stream().mapToInt(ProxyInstanceInfo::getOnlinePlayers).sum();
    }

    @Override
    public String getNickFromUuid(final UUID playerId)
    {
        return this.playersData.uuidToUsername(playerId);
    }

    @Override
    public UUID getUuidFromNick(final String nick)
    {
        return this.playersData.usernameToUuid(nick);
    }

    @Override
    public boolean isOnline(final String nick)
    {
        return this.onlinePlayerValue(nick).isAvailable();
    }

    @Override
    public boolean isOnline(final UUID uuid)
    {
        return this.isOnline(this.getNickFromUuid(uuid));
    }

    @Override
    public boolean access(final String nick, final Consumer<IPlayer> modifier)
    {
        //noinspection unchecked
        return this.access(nick, (Consumer) modifier, (Consumer) modifier);
    }

    @Override
    public boolean access(final UUID uuid, final Consumer<IPlayer> modifier)
    {
        return this.access(this.getNickFromUuid(uuid), modifier);
    }

    @Override
    public boolean access(final String nick, final Consumer<IOnlinePlayer> modifierOnline, final Consumer<IOfflinePlayer> modifierOffline)
    {
        final Value<IOnlinePlayer> onlinePlayerValue = this.onlinePlayerValue(nick);
        try
        {
            onlinePlayerValue.lock();
            final IOnlinePlayer player = onlinePlayerValue.get();
            if (player != null)
            {
                modifierOnline.accept(player);
                onlinePlayerValue.set(player);
                return true;
            }
        }
        finally
        {
            onlinePlayerValue.unlock();
        }

        final Value<IOfflinePlayer> offlinePlayerValue = this.playersData.getOfflinePlayerValue(nick);
        if (offlinePlayerValue == null)
        {
            return false;
        }

        try
        {
            offlinePlayerValue.lock();
            final IOfflinePlayer player = offlinePlayerValue.get();
            if (player != null)
            {
                modifierOffline.accept(player);
                this.playersData.savePlayer(player);
                return true;
            }
        }
        finally
        {
            offlinePlayerValue.unlock();
        }
        return false;
    }

    @Override
    public boolean access(final UUID uuid, final Consumer<IOnlinePlayer> modifierOnline, final Consumer<IOfflinePlayer> modifierOffline)
    {
        return this.access(this.getNickFromUuid(uuid), modifierOnline, modifierOffline);
    }

    @Override
    public void ifOnline(final String nick, final Consumer<IOnlinePlayer> onlineAction)
    {
        final Value<IOnlinePlayer> onlinePlayerValue = this.onlinePlayerValue(nick);
        try
        {
            onlinePlayerValue.lock();
            onlinePlayerValue.ifPresent(onlineAction);
        }
        finally
        {
            onlinePlayerValue.unlock();
        }
    }

    @Override
    public void ifOnline(final UUID uuid, final Consumer<IOnlinePlayer> onlineAction)
    {
        this.ifOnline(this.getNickFromUuid(uuid), onlineAction);
    }

    @Override
    public IPlayerTransaction transaction(final UUID playerId) throws PlayerNotFoundException
    {
        final String playerName = this.getNickFromUuid(playerId);
        if (playerName == null)
        {
            throw new PlayerNotFoundException(playerId);
        }
        final Lock lock = this.getMultiLock(playerName, playerId);
        lock.lock();

        final Value<IOnlinePlayer> onlinePlayer = this.onlinePlayerValue(playerName);
        if (onlinePlayer.isAvailable())
        {
            return new PlayerTransactionImpl(onlinePlayer, lock, player ->
            {
                onlinePlayer.set((IOnlinePlayer) player);
            });
        }

        final Value<IOfflinePlayer> offlinePlayer = this.playersData.getOfflinePlayerValue(playerId);
        if (offlinePlayer.isAvailable())
        {
            return new PlayerTransactionImpl(offlinePlayer, lock, this.playersData::savePlayer);
        }

        lock.unlock();
        throw new PlayerNotFoundException(playerId);
    }

    @Override
    public IPlayerTransaction transaction(final String playerName) throws PlayerNotFoundException
    {
        final UUID playerId = this.getUuidFromNick(playerName);
        if (playerId == null)
        {
            throw new PlayerNotFoundException(playerName);
        }
        final Lock lock = this.getMultiLock(playerName, playerId);
        lock.lock();

        final Value<IOnlinePlayer> onlinePlayer = this.onlinePlayerValue(playerName);
        if (onlinePlayer.isAvailable())
        {
            return new PlayerTransactionImpl(onlinePlayer, lock, player ->
            {
                onlinePlayer.set((IOnlinePlayer) player);
            });
        }

        final Value<IOfflinePlayer> offlinePlayer = this.playersData.getOfflinePlayerValue(playerId);
        if (offlinePlayer.isAvailable())
        {
            return new PlayerTransactionImpl(offlinePlayer, lock, this.playersData::savePlayer);
        }

        lock.unlock();
        throw new PlayerNotFoundException(playerName);
    }

    @Override
    public Unsafe unsafe()
    {
        return this.unsafe;
    }

    class PlayersManagerUnsafeImpl implements Unsafe
    {
        @Override
        public Value<IOnlinePlayer> getOnline(final String nick)
        {
            return PlayersManagerImpl.this.onlinePlayerValue(nick);
        }

        @Override
        public Value<IOnlinePlayer> getOnline(final UUID uuid)
        {
            return PlayersManagerImpl.this.onlinePlayerValue(uuid);
        }

        @Override
        public IOfflinePlayer getOffline(final String nick)
        {
            return PlayersManagerImpl.this.playersData.getOfflinePlayer(nick);
        }

        @Override
        public IOfflinePlayer getOffline(final UUID uuid)
        {
            return PlayersManagerImpl.this.playersData.getOfflinePlayer(uuid);
        }
    }

    private Value<IOnlinePlayer> onlinePlayerValue(final UUID uuid)
    {
        return this.onlinePlayerValue(this.getNickFromUuid(uuid));
    }

    private Value<IOnlinePlayer> onlinePlayerValue(final String nick)
    {
        //noinspection unchecked
        return (Value) this.observer.get(OnlinePlayerImpl.class, PLAYERS + nick.toLowerCase(Locale.ROOT));
    }

    private Lock getMultiLock(final String nick, final UUID uuid)
    {
        return this.observer.getMultiLock("lock:players:" + nick.toLowerCase(Locale.ENGLISH), "lock:offlineplayers:" + uuid);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
