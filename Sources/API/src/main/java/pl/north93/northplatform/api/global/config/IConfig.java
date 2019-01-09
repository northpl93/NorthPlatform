package pl.north93.northplatform.api.global.config;

import javax.annotation.Nullable;

import java.util.Optional;
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
     * Zwraca aktualny obiekt przechowujacy dane configu.
     * Moze byc nullem jesli config nie jest zaladowany.
     *
     * @return Obiekt przechowujacy dane configu.
     */
    @Nullable
    T get();

    /**
     * Zwraca aktualny obiekt przechowujacy dane configu
     * opakowany w {@link Optional}.
     *
     * @return Obiekt przechowujacy dane configu jako Optional.
     */
    Optional<T> getOptional();

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
    default void update(final Consumer<T> update)
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
