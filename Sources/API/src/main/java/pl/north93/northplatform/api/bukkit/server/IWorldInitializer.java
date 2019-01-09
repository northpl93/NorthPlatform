package pl.north93.northplatform.api.bukkit.server;

import java.io.File;

import org.bukkit.World;

/**
 * Klasy implementujące ten interfejs służą do inicjacji różnych
 * rzeczy podczas wczytywania świata. Są one automatycznie wykrywane
 * i rejestrowane.
 */
public interface IWorldInitializer
{
    /**
     * Metoda wywołuje się podczas rejestrowania initializera i po wczytaniu każdego
     * kolejnego świata.
     *
     * @param world Swiat który należy zainicjować.
     * @param directory Katalog świata.
     */
    void initialiseWorld(World world, File directory);
}
