package pl.north93.zgame.api.global.config;

/**
 * Reprezentuje dana instancje configu w sieci.
 * @param <T> typ klasy configu.
 */
public interface IConfig<T>
{
    String getId();

    T get();

    void reload();
}
