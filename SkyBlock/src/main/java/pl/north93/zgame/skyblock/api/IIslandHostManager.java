package pl.north93.zgame.skyblock.api;

import java.util.UUID;

/**
 * Odpowiada za zarządzanie konkretną instancją hosta wysp.
 */
public interface IIslandHostManager
{
    // Metoda wywoływana gdy wyspa ma zostać utworzona na tym serwerze.
    void islandAdded(UUID islandId);

    // Metoda wywoływana gdy wyspa ma zostaćusunięta.
    void islandRemoved(UUID islandId);

    // Metoda wywoływana gdy trzeba zaktualizować informacje o wyspie.
    void islandDataChanged(UUID islandId);
}
