package pl.north93.zgame.api.global.network.players;

import java.util.UUID;
import java.util.function.Consumer;

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
}
