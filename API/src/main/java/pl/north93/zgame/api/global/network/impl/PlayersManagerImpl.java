package pl.north93.zgame.api.global.network.impl;

import static pl.north93.zgame.api.global.redis.RedisKeys.PLAYERS;


import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.data.players.IPlayersData;
import pl.north93.zgame.api.global.messages.ProxyInstanceInfo;
import pl.north93.zgame.api.global.network.IOfflinePlayer;
import pl.north93.zgame.api.global.network.IOnlinePlayer;
import pl.north93.zgame.api.global.network.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;

public class PlayersManagerImpl implements IPlayersManager
{
    private final NetworkManager      networkManager;
    private final IPlayersData        playersData;
    private final IObservationManager observer;

    public PlayersManagerImpl(final NetworkManager networkManager, final IPlayersData playersData, final IObservationManager observer)
    {
        this.networkManager = networkManager;
        this.playersData = playersData;
        this.observer = observer;
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

    private Value<IOnlinePlayer> onlinePlayerValue(final UUID uuid)
    {
        return this.onlinePlayerValue(this.getNickFromUuid(uuid));
    }

    private Value<IOnlinePlayer> onlinePlayerValue(final String nick)
    {
        //noinspection unchecked
        return (Value) this.observer.get(OnlinePlayerImpl.class, PLAYERS + nick.toLowerCase(Locale.ROOT));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
