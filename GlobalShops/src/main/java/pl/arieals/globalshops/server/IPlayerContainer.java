package pl.arieals.globalshops.server;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.Map;

import org.bukkit.entity.Player;

import pl.arieals.globalshops.shared.Item;
import pl.arieals.globalshops.shared.ItemsGroup;

public interface IPlayerContainer
{
    /**
     * Zwraca instancja gracza wrapowana przez ten {@link IPlayerContainer}.
     *
     * @return instancja bukkitowego gracza.
     */
    Player getBukkitPlayer();

    /**
     * Zwraca liste itemow zakupionych z danej grupy.
     *
     * @param group grupa z ktorej wylistowac kupione przedmioty.
     * @return lista kupionych przedmiotow z grupy.
     */
    Collection<Item> getBoughtItems(ItemsGroup group);
    
    /**
     * 
     * @param group grupa z ktorej wylistowac kupione przedmioty.
     * @return mapa zawierajaca obecny poziom dla danego przedmiotu.
     */
    Map<Item, Integer> getBoughtItemsLevel(ItemsGroup group);

    /**
     * Sprawdza czy gracz ma kupiony dany przedmiot na dowolnym poziomie.
     *
     * @param item przedmiot do sprawdzenia.
     * @return true jesli gracz posiada dany przedmiot.
     */
    boolean hasBoughtItem(Item item);

    /**
     * Sprawdza czy gracz ma kupiony dany przedmiot na danym poziomie. 
     *
     * @param item przedmiot do sprawdzenia.
     * @param level poziom przedmiotu do sprawdzenia.
     * @return true jeżeli gracz posiada dany przedmiot na danym poziomie.
     */
    boolean hasBoughtItemAtLevel(Item item, int level);
    
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
     * Oznacza dany przedmiot jako kupiony.
     *
     * @param item przedmiot do kupienia.
     * @return Czy udalo sie kupic (czy gracz nie posiada juz takiego).
     */
    default boolean addItem(Item item)
    {
        return this.addItem(item, 1);
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
     * @return Czy udalo sie dodac przedmiot (czy gracz nie posiada juz takiego).
     */
    boolean addItem(Item item, int level);

    /**
     * Podnosi poziom danego przedmiotu o 1.
     * Gdy sie nie uda (np. gracz ma juz maksymalny poziom) zostanie
     * zwrocone false.
     *
     * @param item przedmiot ktoremu podnosimy poziom.
     * @return True jesli sie udalo, przeciwnie false.
     */
    boolean bumpItemLevel(Item item);

    /**
     * Podnosi ilosc posiadanych przez gracza odlamkow o podana ilosc.
     * Podana wartosc musi miescic sie w zakresie 1-100.
     * Metoda automatycznie uaktywni przedmiot jesli gracz uzyska 100 odlamkow.
     *
     * @param item Przedmiot do ktorego przypisujemy odlamki.
     * @param amount Ilosc odlamkow.
     */
    void addShards(Item item, int amount);

    /**
     * Zwraca ilosc posiadanych przez gracza odlamkow.
     *
     * @param item Przedmiot dla ktorego sa sprawdzane odlamki.
     * @return Ilosc posiadanych odlamkow dango przedmiotu.
     *         Miesci sie w zakresie 0-99
     */
    int getShards(Item item);

    /**
     * Zwraca wybrany przez gracza item jesli typ grupy to SINGLE_PICK.
     * Jesli typ grupy to MULTI_BUY, wtedy zostanie rzucony wyjatek.
     *
     * @throws IllegalArgumentException gdy grupa jest typu MULTI_BUY.
     * @return wybrany item z danej grupy.
     */
    @Nullable Item getActiveItem(ItemsGroup group);

    /**
     * Oznacza dany przedmiot jako aktywny.
     * Przedmiot musi nalezec do grupy typu SINGLE_PICK,
     * w przeciwnym wypadku zostanie rzucony wyjatek.
     *
     * @see #getActiveItem(ItemsGroup)
     * @throws NullPointerException Gdy przedmiot jest nullem.
     * @throws IllegalArgumentException Gdy przedmiot nalezy do grupy MULTI_BUY.
     * @throws IllegalStateException Gdy gracz nie ma kupionego danego przedmiotu.
     * @param item przedmiot ktory oznaczyc jako aktywny w grupie.
     */
    void markAsActive(Item item);

    /**
     * Resetuje aktywny przedmiot w danej grupie.
     * Grupa musi byc typu SINGLE_PICK,
     * w przeciwnym wypadku zostanie rzucony wyjatek.
     *
     * @throws NullPointerException Gdy grupa przedmiotow jest nullem.
     * @throws IllegalArgumentException Gdy przedmiot nalezy do grupy MULTI_BUY.
     * @param group Grupa w ktorej zresetowac (ustawic na null) aktywny przedmiot.
     */
    void resetActiveItem(ItemsGroup group);
}
