package pl.north93.zgame.api.bukkit.player;

import static pl.north93.zgame.api.bukkit.player.Helper.bukkitPlayers;


import java.util.UUID;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messageable;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerProxyData;
import pl.north93.zgame.api.global.network.server.joinaction.IServerJoinAction;
import pl.north93.zgame.api.global.permissions.Group;

/**
 * Interfejs rozszerzajacy Bukkitowego gracza i dodajacy przydatne
 * metody do zarzadzania graczem w sieci.
 * <p>
 * Nalezy zwrocic uwage, ze wiekszosc metod nie pozwoli na
 * edycje danych i sa one nieatomowe (np. podczas Twoich dzialan
 * gracz moze zmienic serwer)
 */
public interface INorthPlayer extends Player, Messageable
{
    static INorthPlayer get(final UUID playerId)
    {
        return bukkitPlayers.getPlayer(playerId);
    }

    static INorthPlayer get(final String nick)
    {
        return bukkitPlayers.getPlayer(nick);
    }

    static INorthPlayer wrap(final Player player)
    {
        return bukkitPlayers.getPlayer(player);
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
    void connectTo(ServerProxyData server, IServerJoinAction... actions);

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

    @Override // trzeba bylo dodac zeby nie bylo bledu kompilacji
    void sendMessage(final String message);
}

final class Helper
{
    @Inject
    static IBukkitPlayers bukkitPlayers;
}