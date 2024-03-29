package pl.north93.northplatform.api.bukkit.player;

import static pl.north93.northplatform.api.bukkit.player.Helper.bukkitPlayers;


import javax.annotation.Nullable;

import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.northplatform.api.bukkit.player.event.PlayerPlatformLocaleChangedEvent;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messageable;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.metadata.Metadatable;
import pl.north93.northplatform.api.global.network.players.IPlayerTransaction;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.global.network.server.Server;
import pl.north93.northplatform.api.global.network.server.joinaction.IServerJoinAction;
import pl.north93.northplatform.api.global.permissions.Group;

/**
 * Interfejs rozszerzajacy Bukkitowego gracza i dodajacy przydatne
 * metody do zarzadzania graczem w sieci.
 * <p>
 * Nalezy zwrocic uwage, ze wiekszosc metod nie pozwoli na
 * edycje danych i sa one nieatomowe (np. podczas Twoich dzialan
 * gracz moze zmienic serwer)
 */
public interface INorthPlayer extends Player, Metadatable, Messageable
{
    static INorthPlayer get(final UUID playerId)
    {
        return bukkitPlayers.getPlayer(playerId);
    }

    static INorthPlayer get(final String nick)
    {
        return bukkitPlayers.getPlayer(nick);
    }

    static INorthPlayer getExact(final String exactNick)
    {
        return bukkitPlayers.getPlayerExact(exactNick);
    }

    static INorthPlayer wrap(final Player player)
    {
        return bukkitPlayers.getPlayer(player);
    }

    static INorthPlayer wrap(final NorthCommandSender northCommandSender)
    {
        return bukkitPlayers.getPlayer(northCommandSender);
    }

    /**
     * Zamienia podaną instancję gracza na instancję CraftPlayer.
     * Ta metoda jest przydatna i bezpieczniejsza niż castowanie ponieważ nie wiadomo
     * czy Player jest CraftPlayerem czy NorthPlayerem.
     *
     * @param player Instancja gracza do zamienienia w CraftPlayer.
     * @return CraftPlayer danego gracza.
     */
    static CraftPlayer asCraftPlayer(final Player player)
    {
        return bukkitPlayers.getCraftPlayer(player);
    }

    /**
     * Zwraca instance CraftPlayera wrapowanego gracza.
     * Przydatne poniewaz castowanie sie tu nie uda.
     *
     * @return CraftPlayer
     */
    CraftPlayer getCraftPlayer();

    @Nullable
    <T> T getPlayerData(Class<T> clazz);

    <T> T getPlayerData(Class<T> clazz, Function<INorthPlayer, T> producer);

    <T> void setPlayerData(T data);

    <T> void setPlayerData(Class<T> clazz, T data);

    void removePlayerData(Class<?> clazz);

    /**
     * Zwraca Identity tego gracza, czyli lekki obiekt służący do
     * identyfikacji gracza.
     *
     * @see Identity
     * @return Identity tego gracza.
     */
    default Identity getIdentity()
    {
        return Identity.of(this);
    }

    /**
     * Otwiera transakcje dostepu do sieciowych danych gracza.
     * Umozliwia takze bezpieczna, atomowa ich edycje.
     * Zalecany sposob uzycia to try/catch-with-resources
     * {@code try (final PlayerTransaction t = iNorthPlayer.openTransaction()){} }
     */
    IPlayerTransaction openTransaction();

    /**
     * @return czy gracz gra jako premium.
     */
    boolean isPremium();

    /**
     * @return serwer na ktorym jest gracz.
     */
    Server getCurrentServer();

    /**
     * @return grupa tego gracza.
     */
    Group getGroup();

    /**
     * Laczy z wybranym serwerem.
     * @param server serwer z ktorym chcemy polaczyc gracza.
     * @param actions obiekty akcji do wykonania po wejsciu na serwer.
     */
    void connectTo(Server server, IServerJoinAction... actions);

    /**
     * Wrzuca gracza do danej grupy serwerow, bungee odpowiada za wybranie serwera.
     * @param serversGroupName Nazwa grupy serwerow.
     * @param actions obiekty akcji do wykonania po wejsciu na serwer.
     */
    void connectTo(String serversGroupName, IServerJoinAction... actions);

    /**
     * Sprawdza czy posiadamy lokalnie zcachowane sieciowe dane gracza
     * (czyli wszystkie dostępne w tym interfejsie).
     *
     * @return True jeśli posiadamy lokalne cache danych gracza.
     */
    boolean isDataCached();

    /**
     * Changes player's locale. Triggers {@link PlayerPlatformLocaleChangedEvent}.
     * This method opens transaction, use in only in asynchronous context!
     *
     * @param locale New locale.
     */
    void updateLocale(Locale locale);

    /**
     * Konwertuje komponent na tekst legacy i wysyła go na action bar gracza.
     *
     * @param component Komponent do wysłania na action bar gracza.
     */
    default void sendActionBar(final BaseComponent component)
    {
        this.sendActionBar(component.toLegacyText());
    }

    /**
     * Wysyła przetłumaczony tekst na action bar gracza.
     *
     * @param messagesBox Obiekt przechowujący wiadomości.
     * @param key Klucz wiadomości.
     * @param params Parametry wiadomości.
     */
    default void sendActionBar(final MessagesBox messagesBox, final String key, final Object... params)
    {
        this.sendActionBar(messagesBox.getString(this.getMyLocale(), key, params));
    }

    @Override // trzeba bylo dodac zeby nie bylo bledu kompilacji
    void sendMessage(final String message);

    @Override // trzeba bylo dodac zeby nie bylo bledu kompilacji
    void sendMessage(BaseComponent component);
}

final class Helper
{
    @Inject
    static IBukkitPlayers bukkitPlayers;
}