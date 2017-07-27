package pl.arieals.globalshops.server;

import org.bukkit.entity.Player;

import pl.arieals.globalshops.shared.Item;
import pl.arieals.globalshops.shared.ItemsGroup;

/**
 * API klienta globalnych sklepow.
 * Udostepnia dane o graczach i ich zakupionych przedmiotach.
 */
public interface IGlobalShops
{
    /**
     * Zwraca grupe o podanym ID.
     *
     * @param id ID grupy.
     * @return grupa o podanym ID.
     */
    ItemsGroup getGroup(String id);

    /**
     * Zwraca przedmiot o podanym ID.
     *
     * @param id ID przedmiotu.
     * @return przedmiot o podanym ID.
     */
    Item getItem(String id);

    /**
     * Zwraca obiekt udostepniajacy dane o kupionych itemach
     * i umozliwiajacy modyfikowanie ich.
     *
     * @param player gracz dla ktorego zwrocic obiekt.
     * @return obiekt umozliwiajacy dostep do danych gracza.
     */
    IPlayerContainer getPlayer(Player player);
}
