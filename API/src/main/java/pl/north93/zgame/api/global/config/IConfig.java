package pl.north93.zgame.api.global.config;

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
     *
     * @param newValue Nowa wartosc configu.
     */
    void update(T newValue);

    /**
     * Odczytuje ponownie konfiguracje z dysku.
     */
    void reload();
}
