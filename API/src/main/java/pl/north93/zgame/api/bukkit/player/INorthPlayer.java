package pl.north93.zgame.api.bukkit.player;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

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
}
