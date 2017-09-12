package pl.north93.zgame.api.global.network.players;

import java.util.UUID;
import java.util.function.Consumer;

import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;
import pl.north93.zgame.api.global.redis.observable.Value;

public interface IPlayersManager
{
    String getNickFromUuid(UUID playerId);

    UUID getUuidFromNick(String nick);

    boolean isOnline(Identity identity);

    default boolean isOnline(String nick)
    {
        return this.isOnline(Identity.create(null, nick, null));
    }

    default boolean isOnline(UUID uuid)
    {
        return this.isOnline(Identity.create(uuid, null, null));
    }

    boolean access(Identity identity, Consumer<IPlayer> modifier);

    default boolean access(String nick, Consumer<IPlayer> modifier)
    {
        return this.access(Identity.create(null, nick, null), modifier);
    }

    default boolean access(UUID uuid, Consumer<IPlayer> modifier)
    {
        return this.access(Identity.create(uuid, null, null), modifier);
    }

    boolean access(Identity identity, Consumer<IOnlinePlayer> modifierOnline, Consumer<IOfflinePlayer> modifierOffline);

    default boolean access(String nick, Consumer<IOnlinePlayer> modifierOnline, Consumer<IOfflinePlayer> modifierOffline)
    {
        return this.access(Identity.create(null, nick, null), modifierOnline, modifierOffline);
    }

    default boolean access(UUID uuid, Consumer<IOnlinePlayer> modifierOnline, Consumer<IOfflinePlayer> modifierOffline)
    {
        return this.access(Identity.create(uuid, null, null), modifierOnline, modifierOffline);
    }

    void ifOnline(String nick, Consumer<IOnlinePlayer> onlineAction);

    void ifOnline(UUID uuid, Consumer<IOnlinePlayer> onlineAction);

    IPlayerTransaction transaction(Identity identity) throws PlayerNotFoundException;

    default IPlayerTransaction transaction(UUID playerId) throws PlayerNotFoundException
    {
        return this.transaction(Identity.create(playerId, null, null));
    }

    default IPlayerTransaction transaction(String playerName) throws PlayerNotFoundException
    {
        return this.transaction(Identity.create(null, playerName, null));
    }

    Unsafe unsafe();

    interface Unsafe
    {
        IPlayer get(Identity identity); // do not modify returned instance. It will be not saved!

        Value<IOnlinePlayer> getOnline(String nick);

        Value<IOnlinePlayer> getOnline(UUID uuid);

        IOfflinePlayer getOffline(String nick); // do not modify returned instance. It will be not saved!

        IOfflinePlayer getOffline(UUID nick); // do not modify returned instance. It will be not saved!
    }
}
