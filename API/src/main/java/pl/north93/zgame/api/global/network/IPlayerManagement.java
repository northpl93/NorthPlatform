package pl.north93.zgame.api.global.network;

import java.util.UUID;

import pl.north93.zgame.api.global.network.players.IOfflinePlayer;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.redis.observable.Value;

public interface IPlayerManagement
{
    int onlinePlayersCount();

    String getNickFromUuid(UUID playerId);

    UUID getUuidFromNick(String nick);

    Value<IOnlinePlayer> getOnlinePlayer(String nick);

    Value<IOnlinePlayer> getOnlinePlayer(UUID playerUuid);

    IOfflinePlayer getOfflinePlayer(UUID playerUuid);

    IOfflinePlayer getOfflinePlayer(String nick);

    void savePlayer(IPlayer player);

    boolean isOnline(String nick);
}
