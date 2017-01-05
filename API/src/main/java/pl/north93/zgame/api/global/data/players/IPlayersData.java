package pl.north93.zgame.api.global.data.players;

import java.util.UUID;

import pl.north93.zgame.api.global.network.IOfflinePlayer;
import pl.north93.zgame.api.global.network.IPlayer;
import pl.north93.zgame.api.global.network.impl.OnlinePlayerImpl;
import pl.north93.zgame.api.global.redis.observable.Value;

public interface IPlayersData
{
    Value<OnlinePlayerImpl> loadPlayer(UUID uuid, String name, Boolean premium, String proxyId);

    IOfflinePlayer getOfflinePlayer(UUID uuid);

    void savePlayer(IPlayer player);

    UUID usernameToUuid(String username);

    String uuidToUsername(UUID playerUuid);
}
