package pl.arieals.globalshops.server;

import javax.annotation.Nullable;

import java.util.Collection;

import pl.arieals.globalshops.shared.Item;
import pl.arieals.globalshops.shared.ItemsGroup;

public interface IPlayerContainer
{
    /**
     * Zwraca liste itemow zakupionych z danej grupy.
     *
     * @param group grupa z ktorej wylistowac kupione przedmioty.
     * @return lista kupionych przedmiotow z grupy.
     */
    Collection<Item> getBoughtItems(ItemsGroup group);

    /**
     * Sprawdza czy gracz ma kupiony dany przedmiot na dowolnym poziomie.
     *
     * @param item przedmiot do sprawdzenia.
     * @return true jesli gracz posiada dany przedmiot.
     */
    boolean hasBoughtItem(Item item);

    /**
     * Zwraca poziom danego przedmiotu.
     * W przypadku gdy przedmiot nie jest kupiony zwroci 0.
     * Jesli przedmiot nie obsluguje poziomow, najwyzsza
     * wartoscia bedzie 1.
     *
     * @param item przedmiot dla ktorego sprawdzamy poziom.
     * @return poziom zakupionego przedmiotu, zgodnie z opisem wyzej.
     */
    int getBoughtItemLevel(Item item);

    /**
     * Sprawdza czy gracz posiada kupiony maksymalny poziom danego przedmiotu.
     *
     * @param item przedmiot do sprawdzenia.
     * @return true jesli gracz ma kupiony najwyzszy poziom.
     */
    boolean hasMaxLevel(Item item);

    /**
     * Zwraca wybrany przez gracza item jesli typ grupy to SINGLE_PICK.
     * Jesli typ grupy to MULTI_BUY, wtedy zostanie rzucony wyjatek.
     *
     * @throws IllegalArgumentException gdy grupa jest typu MULTI_BUY.
     * @return wybrany item z danej grupy.
     */
    @Nullable Item getActiveItem(ItemsGroup group);

    /**
     * Oznacza dany przedmiot jako kupiony.
     *
     * @param item przedmiot do kupienia.
     */
    default void addItem(Item item)
    {
        this.addItem(item, 1);
    }

    /**
     * Oznacza dany przedmiot jako kupiony na danym poziomie.
     * Poziom:
     * <ul>
     *     <li>nie moze byz nizszy niz 1
     *     <li>nie moze byc nizszy lub rowny aktualnego kupionego przedmiotu
     *     <li>nie moze byc wyzszy niz maksymalny poziom przedmiotu
     *
     * @param item przedmiot ktory oznaczamy jako kupiony.
     * @param level poziom na ktorym oznaczamy jako kupiony.
     */
    void addItem(Item item, int level);

    /**
     * Oznacza dany przedmiot jako aktywny.
     * Przedmiot musi nalezec do grupy typu SINGLE_PICK,
     * w przeciwnym wypadku zostanie rzucony wyjatek.
     *
     * @throws IllegalArgumentException Gdy przedmiot nalezy do grupy MULTI_BUY.
     * @throws IllegalStateException Gdy gracz nie ma kupionego danego przedmiotu.
     * @param item przedmiot ktory oznaczyc jako aktywny w grupie.
     */
    void markAsActive(Item item);
}
