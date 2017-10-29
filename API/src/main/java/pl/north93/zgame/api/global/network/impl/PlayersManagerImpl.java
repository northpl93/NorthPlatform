package pl.north93.zgame.api.global.network.impl;

import static pl.north93.zgame.api.global.redis.RedisKeys.PLAYERS;


import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;
import pl.north93.zgame.api.global.network.players.IOfflinePlayer;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.network.proxy.IProxyRpc;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Lock;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.Targets;

class PlayersManagerImpl implements IPlayersManager
{
    /*default*/ static PlayersManagerImpl INSTANCE;
    private final NetworkManager      networkManager;
    private final PlayerCacheImpl     playerCache;
    private final PlayersDataManager  playersDataManager;
    private final IObservationManager observer;
    private final IRpcManager         rpcManager;
    private final Unsafe              unsafe;

    public PlayersManagerImpl(final NetworkManager networkManager, final IObservationManager observer, final IRpcManager rpcManager)
    {
        INSTANCE = this;
        this.rpcManager = rpcManager;
        this.networkManager = networkManager;
        this.observer = observer;
        this.playerCache = new PlayerCacheImpl();
        this.playersDataManager = new PlayersDataManager(this);
        this.unsafe = new PlayersManagerUnsafeImpl();
    }

    /*
     * Wewnetrzna metoda implementacji.
     * Uzywana w OnlinePlayerImpl.
     */
    public IProxyRpc getPlayerProxyRpc(final IOnlinePlayer onlinePlayer)
    {
        return this.rpcManager.createRpcProxy(IProxyRpc.class, Targets.proxy(onlinePlayer.getProxyId()));
    }

    @Override
    public String getNickFromUuid(final UUID playerId)
    {
        Preconditions.checkNotNull(playerId);

        return this.playersDataManager.uuidToUsername(playerId);
    }

    @Override
    public UUID getUuidFromNick(final String nick)
    {
        Preconditions.checkNotNull(nick);

        return this.playersDataManager.usernameToUuid(nick);
    }

    @Override
    public boolean isOnline(final Identity identity)
    {
        if (identity.getNick() != null)
        {
            return this.onlinePlayerValue(identity.getNick()).isAvailable();
        }
        else if (identity.getUuid() != null)
        {
            final String nickFromUuid = this.getNickFromUuid(identity.getUuid());
            return this.onlinePlayerValue(nickFromUuid).isAvailable();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean access(final Identity identity, final Consumer<IPlayer> modifier)
    {
        return this.access(identity, (Consumer) modifier, (Consumer) modifier);
    }

    @Override
    public boolean access(final Identity identity, final Consumer<IOnlinePlayer> modifierOnline, final Consumer<IOfflinePlayer> modifierOffline)
    {
        final Identity completeIdentity = this.completeIdentity(identity);
        final Value<IOnlinePlayer> onlinePlayerValue = this.onlinePlayerValue(completeIdentity.getNick());
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

        final Value<IOfflinePlayer> offlinePlayerValue = this.playersDataManager.getOfflinePlayerValue(identity.getUuid());
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
                this.playersDataManager.savePlayer(player);
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
    public IPlayerTransaction transaction(final Identity identity) throws PlayerNotFoundException
    {
        final Identity completeIdentity = this.completeIdentity(identity);

        final Value<IOnlinePlayer> onlinePlayer = this.onlinePlayerValue(completeIdentity.getNick());
        final Value<IOfflinePlayer> offlinePlayer = this.playersDataManager.getOfflinePlayerValue(completeIdentity.getUuid());

        final Lock lock = this.getMultiLock(onlinePlayer, offlinePlayer);
        lock.lock();

        if (onlinePlayer.isAvailable())
        {
            return new PlayerTransactionImpl(onlinePlayer, lock, player ->
            {
                onlinePlayer.set((IOnlinePlayer) player);
            });
        }
        if (offlinePlayer.isAvailable())
        {
            return new PlayerTransactionImpl(offlinePlayer, lock, this.playersDataManager::savePlayer);
        }

        lock.unlock();
        throw new PlayerNotFoundException(completeIdentity.getNick());
    }

    @Override
    public IPlayerCache getCache()
    {
        return this.playerCache;
    }

    @Override
    public Unsafe unsafe()
    {
        return this.unsafe;
    }

    class PlayersManagerUnsafeImpl implements Unsafe
    {
        @Override
        public IPlayer get(final Identity identity)
        {
            final Identity completed = PlayersManagerImpl.this.completeIdentity(identity);

            final Value<IOnlinePlayer> online = this.getOnline(completed.getNick());
            if (online.isCached() || online.isAvailable())
            {
                return online.get();
            }

            return this.getOffline(completed.getNick());
        }

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
            return PlayersManagerImpl.this.playersDataManager.getOfflinePlayer(nick);
        }

        @Override
        public IOfflinePlayer getOffline(final UUID uuid)
        {
            return PlayersManagerImpl.this.playersDataManager.getOfflinePlayer(uuid);
        }
    }

    @Override
    public IPlayersDataManager getInternalData()
    {
        return this.playersDataManager;
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

    private Lock getMultiLock(final Value<IOnlinePlayer> onlineData, final Value<IOfflinePlayer> offlineData)
    {
        return this.observer.getMultiLock(onlineData.getLock(), offlineData.getLock());
    }

    private Identity completeIdentity(final Identity identity)
    {
        final UUID currentUuid = identity.getUuid();
        final String currentNick = identity.getNick();

        if (currentUuid != null && currentNick != null)
        {
            // nic nie musimy uzupelniac
            return identity;
        }
        else if (currentNick == null && currentUuid == null)
        {
            // mamy calkowicie puste identity, nic z nim nie zrobimy
            throw new IllegalArgumentException("Both nick and uuid are null");
        }

        final UUID uuid = currentUuid == null ? this.getUuidFromNick(currentNick) : currentUuid;
        final String nick = currentNick == null ? this.getNickFromUuid(currentUuid) : currentNick;

        return Identity.create(uuid, nick, identity.getDisplayName());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
