package pl.north93.zgame.api.global.network.players;

import javax.annotation.ParametersAreNonnullByDefault;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import pl.north93.zgame.api.global.network.impl.OnlinePlayerImpl;
import pl.north93.zgame.api.global.redis.observable.Value;

/**
 * Glowny interfejs reprezentujacy system zarzadzajacy danymi graczy.
 */
@ParametersAreNonnullByDefault
public interface IPlayersManager
{
    Optional<String> getNickFromUuid(UUID playerId);

    Optional<UUID> getUuidFromNick(String nick);

    /**
     * Uzupełnia dane Identity brakującym nickiem lub UUID.
     * W wypadku gdy obydwa są nullem zostanie rzucony wyjątek.
     *
     * @throws IllegalArgumentException W przypadku gdy jednocześnie nick i UUID są nullem.
     * @throws PlayerNotFoundException W przypadku gdy nie udało się uzupełnić Identity.
     * @param identity Identity do uzupełnienia.
     * @return Identity uzupełnione brakującym nickiem lub UUID.
     */
    Identity completeIdentity(Identity identity) throws PlayerNotFoundException;

    boolean isOnline(Identity identity);

    default boolean isOnline(final String nick)
    {
        return this.isOnline(Identity.create(null, nick));
    }

    default boolean isOnline(final UUID uuid)
    {
        return this.isOnline(Identity.create(uuid, null));
    }

    IPlayerTransaction transaction(Identity identity) throws PlayerNotFoundException;

    default IPlayerTransaction transaction(final UUID playerId) throws PlayerNotFoundException
    {
        return this.transaction(Identity.create(playerId, null));
    }

    default IPlayerTransaction transaction(final String playerName) throws PlayerNotFoundException
    {
        return this.transaction(Identity.create(null, playerName));
    }

    boolean access(Identity identity, Consumer<IPlayer> modifier);

    default boolean access(final String nick, final Consumer<IPlayer> modifier)
    {
        return this.access(Identity.create(null, nick), modifier);
    }

    default boolean access(final UUID uuid, final Consumer<IPlayer> modifier)
    {
        return this.access(Identity.create(uuid, null), modifier);
    }

    boolean access(Identity identity, Consumer<IOnlinePlayer> modifierOnline, Consumer<IOfflinePlayer> modifierOffline);

    default boolean access(final String nick, final Consumer<IOnlinePlayer> modifierOnline, final Consumer<IOfflinePlayer> modifierOffline)
    {
        return this.access(Identity.create(null, nick), modifierOnline, modifierOffline);
    }

    default boolean access(final UUID uuid, final Consumer<IOnlinePlayer> modifierOnline, final Consumer<IOfflinePlayer> modifierOffline)
    {
        return this.access(Identity.create(uuid, null), modifierOnline, modifierOffline);
    }

    void ifOnline(String nick, Consumer<IOnlinePlayer> onlineAction);

    void ifOnline(UUID uuid, Consumer<IOnlinePlayer> onlineAction);

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
        // zwraca gracza online lub offline
        Optional<IPlayer> get(Identity identity); // do not modify returned instance. It will be not saved!

        default IPlayer getNullable(final Identity identity) // metoda pomocnicza do tej wyżej
        {
            return this.get(identity).orElse(null);
        }

        Value<IOnlinePlayer> getOnlineValue(String nick);

        Optional<Value<IOnlinePlayer>> getOnlineValue(UUID uuid);

        Value<IOfflinePlayer> getOfflineValue(UUID uuid);

        Optional<IOfflinePlayer> getOffline(String nick); // do not modify returned instance. It will be not saved!

        Optional<IOfflinePlayer> getOffline(UUID uuid); // do not modify returned instance. It will be not saved!
    }

    IPlayersDataManager getInternalData();

    /**
     * Subsystem zarzadzajacy wczytywaniem i zapisem danych graczy z mongodb.
     */
    interface IPlayersDataManager
    {
        void logPlayerJoin(UUID uuid, String nick, boolean premium, String ip, String bungee);

        /**
         * Wczytuje gracza w stanie offline z nieulotnej bazy danych do bazy danych tymczasowej
         * czyniąc go graczem online.
         *
         * @param uuid UUID wczytywanego gracza.
         * @param name Nick wczytywanego gracza.
         * @param premium Czy ładujemy gracza zweryfikowanego w online mode.
         * @param proxyId Unikalny identyfikator proxy odpowiedzialnego za wczytanie danych gracza
         * @return Wartość reprezentująca gracza online w ulotnej bazie danych.
         * @throws NameSizeMistakeException Gdy wielkość liter gracza offline mode się nie zgadza.
         */
        Value<OnlinePlayerImpl> loadPlayer(UUID uuid, String name, Boolean premium, String proxyId) throws NameSizeMistakeException;

        Optional<Value<IOfflinePlayer>> getOfflinePlayerValue(UUID uuid);

        Optional<Value<IOfflinePlayer>> getOfflinePlayerValue(String nick);

        Optional<IOfflinePlayer> getOfflinePlayer(UUID uuid);

        Optional<IOfflinePlayer> getOfflinePlayer(String nick);

        void savePlayer(IPlayer player);
    }
}
