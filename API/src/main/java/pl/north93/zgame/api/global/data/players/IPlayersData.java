package pl.north93.zgame.api.global.data.players;

import java.util.UUID;

import pl.north93.zgame.api.global.data.players.impl.NameSizeMistakeException;
import pl.north93.zgame.api.global.network.players.IOfflinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.impl.OnlinePlayerImpl;
import pl.north93.zgame.api.global.redis.observable.Value;

public interface IPlayersData
{
    void logPlayerJoin(UUID uuid, String nick, boolean premium, String ip, String bungee);

    Value<OnlinePlayerImpl> loadPlayer(UUID uuid, String name, Boolean premium, String proxyId) throws NameSizeMistakeException;

    Value<IOfflinePlayer> getOfflinePlayerValue(UUID uuid);

    Value<IOfflinePlayer> getOfflinePlayerValue(String nick);

    IOfflinePlayer getOfflinePlayer(UUID uuid);

    IOfflinePlayer getOfflinePlayer(String nick);

    void savePlayer(IPlayer player);

    UUID usernameToUuid(String username);

    String uuidToUsername(UUID playerUuid);
}
