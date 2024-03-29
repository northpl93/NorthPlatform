package pl.north93.northplatform.api.global.network.impl.players;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.network.players.IOfflinePlayer;
import pl.north93.northplatform.api.global.network.players.IOnlinePlayer;
import pl.north93.northplatform.api.global.network.players.IPlayer;
import pl.north93.northplatform.api.global.network.players.IPlayerTransaction;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.global.network.players.PlayerNotFoundException;
import pl.north93.northplatform.api.global.network.proxy.IProxyRpc;
import pl.north93.northplatform.api.global.redis.RedisKeys;
import pl.north93.northplatform.api.global.redis.observable.IObservationManager;
import pl.north93.northplatform.api.global.redis.observable.Lock;
import pl.north93.northplatform.api.global.redis.observable.Value;
import pl.north93.northplatform.api.global.redis.rpc.IRpcManager;
import pl.north93.northplatform.api.global.redis.rpc.Targets;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;

@ParametersAreNonnullByDefault
class PlayersManagerImpl implements IPlayersManager
{
    private final IObservationManager observer;
    private final IRpcManager         rpcManager;
    private final PlayersDataManager  playersDataManager;
    private final Unsafe              unsafe;

    @Bean
    private PlayersManagerImpl(final IObservationManager observer, final IRpcManager rpcManager)
    {
        this.observer = observer;
        this.rpcManager = rpcManager;
        this.playersDataManager = new PlayersDataManager();
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
    public Optional<String> getNickFromUuid(final UUID playerId)
    {
        Preconditions.checkNotNull(playerId);

        return this.playersDataManager.uuidToUsername(playerId);
    }

    @Override
    public Optional<UUID> getUuidFromNick(final String nick)
    {
        Preconditions.checkNotNull(nick);

        return this.playersDataManager.usernameToUuid(nick);
    }

    @Override
    public boolean isOnline(final Identity identity)
    {
        if (identity.getNick() != null)
        {
            return this.nickToOnlinePlayerValue(identity.getNick()).isAvailable();
        }
        else if (identity.getUuid() != null)
        {
            return this.getNickFromUuid(identity.getUuid())
                       .map(this::nickToOnlinePlayerValue)
                       .map(Value::isAvailable)
                       .orElse(false);
        }
        return false;
    }

    @Override
    public IPlayerTransaction transaction(final Identity identity) throws PlayerNotFoundException
    {
        final Identity completeIdentity = this.completeIdentity(identity);

        final Value<IOnlinePlayer> onlinePlayer = Optional.ofNullable(completeIdentity.getNick()).map(this::nickToOnlinePlayerValue).orElse(null);
        final Value<IOfflinePlayer> offlinePlayer = Optional.ofNullable(completeIdentity.getUuid()).flatMap(this.playersDataManager::getOfflinePlayerValue).orElse(null);

        final Lock lock = this.getMultiLock(onlinePlayer, offlinePlayer);
        lock.lock();

        if (onlinePlayer != null && onlinePlayer.isAvailable())
        {
            return new PlayerTransactionImpl(onlinePlayer, lock, player ->
            {
                onlinePlayer.set((IOnlinePlayer) player);
            });
        }
        if (offlinePlayer != null && offlinePlayer.isAvailable())
        {
            return new PlayerTransactionImpl(offlinePlayer, lock, this.playersDataManager::savePlayer);
        }

        lock.unlock();
        throw new PlayerNotFoundException(completeIdentity);
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
        try (final IPlayerTransaction transaction = this.transaction(identity))
        {
            if (transaction.isOnline())
            {
                modifierOnline.accept(transaction.getPlayer());
            }
            else
            {
                modifierOffline.accept(transaction.getPlayer());
            }

            return true;
        }
        catch (final PlayerNotFoundException e)
        {
            return false;
        }
    }

    @Override
    public void ifOnline(final String nick, final Consumer<IOnlinePlayer> onlineAction)
    {
        final Value<IOnlinePlayer> onlinePlayerValue = this.nickToOnlinePlayerValue(nick);
        try (final Lock lock = onlinePlayerValue.lock())
        {
            onlinePlayerValue.ifPresent(onlineAction);
        }
    }

    @Override
    public void ifOnline(final UUID uuid, final Consumer<IOnlinePlayer> onlineAction)
    {
        this.getNickFromUuid(uuid).ifPresent(nick -> this.ifOnline(nick, onlineAction));
    }

    @Override
    public Unsafe unsafe()
    {
        return this.unsafe;
    }

    class PlayersManagerUnsafeImpl implements Unsafe
    {
        @SuppressWarnings("unchecked")
        @Override
        public Optional<IPlayer> get(final Identity identity)
        {
            final Identity completed;
            try
            {
                completed = PlayersManagerImpl.this.completeIdentity(identity);
            }
            catch (final PlayerNotFoundException e)
            {
                return Optional.empty();
            }

            final Optional<IPlayer> online = Optional.ofNullable(completed.getNick()).map(this::getOnlineValue).map(Value::get);
            if (online.isPresent())
            {
                return online;
            }

            return (Optional) this.getOffline(completed.getUuid());
        }

        @Override
        public Value<IOnlinePlayer> getOnlineValue(final String nick)
        {
            return PlayersManagerImpl.this.nickToOnlinePlayerValue(nick);
        }

        @Override
        public Optional<Value<IOnlinePlayer>> getOnlineValue(final UUID uuid)
        {
            return PlayersManagerImpl.this.uuidToOnlinePlayerValue(uuid);
        }

        @Override
        public Value<IOfflinePlayer> getOfflineValue(final UUID uuid)
        {
            // zwróci null tylko w przypadku gdy uuid będzie nullem
            return PlayersManagerImpl.this.playersDataManager.getOfflinePlayerValue(uuid).orElse(null);
        }

        @Override
        public Optional<IOfflinePlayer> getOffline(final String nick)
        {
            return PlayersManagerImpl.this.playersDataManager.getOfflinePlayer(nick);
        }

        @Override
        @Nullable
        public Optional<IOfflinePlayer> getOffline(final UUID uuid)
        {
            return PlayersManagerImpl.this.playersDataManager.getOfflinePlayer(uuid);
        }
    }

    @Override
    public IPlayersDataManager getInternalData()
    {
        return this.playersDataManager;
    }

    private Optional<Value<IOnlinePlayer>> uuidToOnlinePlayerValue(final UUID uuid)
    {
        Preconditions.checkNotNull(uuid);
        return this.getNickFromUuid(uuid).map(this::nickToOnlinePlayerValue);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private Value<IOnlinePlayer> nickToOnlinePlayerValue(final String nick)
    {
        Preconditions.checkNotNull(nick);
        return (Value) this.observer.get(OnlinePlayerImpl.class, RedisKeys.PLAYERS + nick.toLowerCase(Locale.ROOT));
    }

    private Lock getMultiLock(final @Nullable Value<IOnlinePlayer> onlineData, final @Nullable Value<IOfflinePlayer> offlineData)
    {
        if (onlineData != null && offlineData != null)
        {
            return this.observer.getMultiLock(onlineData.getLock(), offlineData.getLock());
        }
        else if (onlineData != null)
        {
            return onlineData.getLock();
        }
        else if (offlineData != null)
        {
            return offlineData.getLock();
        }

        return this.observer.getMultiLock(new Lock[]{});
    }

    @Override
    @Nonnull
    public Identity completeIdentity(final Identity identity) throws PlayerNotFoundException
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
        else
        {
            final Supplier<PlayerNotFoundException> exceptionSupplier = () -> new PlayerNotFoundException(identity);
            if (currentNick != null)
            {
                final UUID uuid = this.getUuidFromNick(currentNick).orElseThrow(exceptionSupplier);
                return Identity.create(uuid, currentNick);
            }
            else
            {
                final String nick = this.getNickFromUuid(currentUuid).orElseThrow(exceptionSupplier);
                return Identity.create(currentUuid, nick);
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
