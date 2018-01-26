package pl.north93.zgame.api.global.network.players;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;
import pl.north93.zgame.api.global.network.impl.OnlinePlayerImpl;
import pl.north93.zgame.api.global.redis.observable.Value;

/**
 * Glowny interfejs reprezentujacy system zarzadzajacy danymi graczy.
 */
public interface IPlayersManager
{
    String getNickFromUuid(UUID playerId);

    UUID getUuidFromNick(String nick);

    /**
     * Uzupełnia dane Identity brakującym nickiem lub UUID.
     * W wypadku gdy obydwa są nullem zostanie rzucony wyjątek.
     *
     * @param identity Identity do uzupełnienia.
     * @return Identity uzupełnione brakującym nickiem lub UUID.
     */
    Identity completeIdentity(Identity identity);

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

    IPlayerCache getCache();

    /**
     * Przedstawia subsystem cachujacy dane pobierane z Mojangu.
     */
    interface IPlayerCache
    {
        /**
         * Pobiera informacje o profilu powiazanym z danym nickiem.
         * W pierwszej kolejnosci odpytana zostanie lokalna baza danych serwera (redis&mongo).
         *
         * @param nick Nick ktory sprawdzamy.
         *             Wielkosc znakow nie ma znaczenia (poniewaz tak samo dziala API Mojang).
         * @return Opcjonalnie informacje o danym nicku. W wypadku braku moze to oznaczac
         *         problem z komunikacja z serwerami Mojangu.
         */
        Optional<UsernameDetails> getNickDetails(String nick);
    }

    Unsafe unsafe();

    /**
     * Niebezpieczne funkcje API systemu uzytkownikow.
     */
    interface Unsafe
    {
        IPlayer get(Identity identity); // do not modify returned instance. It will be not saved!

        Value<IOnlinePlayer> getOnline(String nick);

        Value<IOnlinePlayer> getOnline(UUID uuid);

        IOfflinePlayer getOffline(String nick); // do not modify returned instance. It will be not saved!

        IOfflinePlayer getOffline(UUID nick); // do not modify returned instance. It will be not saved!
    }

    IPlayersDataManager getInternalData();

    /**
     * Subsystem zarzadzajacy wczytywaniem i zapisem danych graczy z mongodb.
     */
    interface IPlayersDataManager
    {
        void logPlayerJoin(UUID uuid, String nick, boolean premium, String ip, String bungee);

        Value<OnlinePlayerImpl> loadPlayer(UUID uuid, String name, Boolean premium, String proxyId) throws NameSizeMistakeException;

        Value<IOfflinePlayer> getOfflinePlayerValue(UUID uuid);

        Value<IOfflinePlayer> getOfflinePlayerValue(String nick);

        IOfflinePlayer getOfflinePlayer(UUID uuid);

        IOfflinePlayer getOfflinePlayer(String nick);

        void savePlayer(IPlayer player);
    }
}
