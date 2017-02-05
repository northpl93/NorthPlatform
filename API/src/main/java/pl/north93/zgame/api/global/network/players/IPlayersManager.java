package pl.north93.zgame.api.global.network.players;

import java.util.UUID;
import java.util.function.Consumer;

import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;
import pl.north93.zgame.api.global.redis.observable.Value;

public interface IPlayersManager
{
    int onlinePlayersCount();

    String getNickFromUuid(UUID playerId);

    UUID getUuidFromNick(String nick);

    boolean isOnline(String nick);

    boolean isOnline(UUID uuid);

    boolean access(String nick, Consumer<IPlayer> modifier);

    boolean access(UUID uuid, Consumer<IPlayer> modifier);

    boolean access(String nick, Consumer<IOnlinePlayer> modifierOnline, Consumer<IOfflinePlayer> modifierOffline);

    boolean access(UUID uuid, Consumer<IOnlinePlayer> modifierOnline, Consumer<IOfflinePlayer> modifierOffline);

    void ifOnline(String nick, Consumer<IOnlinePlayer> onlineAction);

    void ifOnline(UUID uuid, Consumer<IOnlinePlayer> onlineAction);

    IPlayerTransaction transaction(UUID playerId) throws PlayerNotFoundException;

    IPlayerTransaction transaction(String playerName) throws PlayerNotFoundException;

    Unsafe unsafe();

    interface Unsafe
    {
        Value<IOnlinePlayer> getOnline(String nick);

        Value<IOnlinePlayer> getOnline(UUID uuid);

        IOfflinePlayer getOffline(String nick); // do not modify returned instance. It will be not saved!

        IOfflinePlayer getOffline(UUID nick); // do not modify returned instance. It will be not saved!
    }
}
