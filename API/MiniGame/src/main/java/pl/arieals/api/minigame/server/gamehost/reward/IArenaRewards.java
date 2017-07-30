package pl.arieals.api.minigame.server.gamehost.reward;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.players.Identity;

/**
 * Interfejs dostępowy do systemu nagród powiązany z daną areną.
 * Przechowuje liste nagrod otrzymanych przez graczy.
 */
public interface IArenaRewards
{
    /**
     * @return arena powiazana z tym obiektem.
     */
    LocalArena getArena();

    /**
     * Dodaje nagroda dla gracza.
     *
     * @see Identity#of(Player)
     * @param identity gracz ktoremu dajemy nagrode.
     * @param reward nagrode ktora dajemy.
     */
    void addReward(Identity identity, IReward reward);

    /**
     * Zwraca wszystkie nagrody okreslonego gracza.
     *
     * @see Identity#of(Player)
     * @param player gracz ktorego nagrody zwrocic.
     * @return lista nagrod danego gracza.
     */
    Collection<IReward> getRewardsOf(Identity player);

    /**
     * Zwraca nagrody danego gracza pogrupowane wedlug identyfikatora nagrody.
     *
     * @see Identity#of(Player)
     * @see IReward#getId()
     * @param player gracz dla ktorego pobrac nagrody.
     * @return nagrody pogrupowane wedlug identyfikatora nagrody.
     */
    Map<String, List<IReward>> groupRewardsOf(Identity player);

    /**
     * Metoda pomocnicza budujaca i wysylajaca wiadomosc z lista nagrod.
     * <p>
     * Tlumaczenia nagrod powinny byc przechowywane w {@link MessagesBox}
     * w formacie {@code rewards.id_nagrody} np. rewards.elimination
     *
     * @param messagesBox plik z tlumaczeniami
     * @param player gracz dla ktorego tworzymy liste nagrod.
     */
    void renderRewards(MessagesBox messagesBox, Player player);

    /**
     * Resetuje liste nagrod.
     * Uzywane do przygotowania areny do kolejnego cyklu.
     */
    void reset();
}
