package pl.north93.zgame.api.global.config;

import java.util.function.Consumer;

/**
 * Reprezentuje dana instancje configu w sieci.
 * @param <T> typ klasy configu.
 */
public interface IConfig<T>
{
    /**
     * Zwraca unikalny identyfikator tego configu w sieci.
     *
     * @return unikalny identyfikator configu.
     */
    String getId();

    /**
     * Zwraca aktualna klase przechowujaca dane configu.
     *
     * @return Klasa przechowujaca dane configu.
     */
    T get();

    /**
     * Aktualizuje wartosc configu.
     * Nie zapewnia atomowosci transakcji, wiec powinno byc uzywane rozsadnie.
     * Edycja z wielu miejsce moze spowodowac utrate danych.
     *
     * @param newValue Nowa wartosc configu.
     */
    void update(T newValue);

    /**
     * Aktualizuje wartosc configu podana funkcja.
     * Nie zapewnia atomowosci transakcji, wiec powinno byc uzywane rozsadnie.
     * Edycja z wielu miejsce moze spowodowac utrate danych.
     *
     * @param update Funkcja aktualizujaca.
     */
    default void update(Consumer<T> update)
    {
        final T current = this.get();
        update.accept(current);
        this.update(current);
    }

    /**
     * Odczytuje ponownie konfiguracje z dysku.
     */
    void reload();
}
