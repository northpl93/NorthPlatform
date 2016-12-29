package pl.north93.zgame.skyblock.api;

import java.util.UUID;

import pl.north93.zgame.api.global.redis.rpc.annotation.DoNotWaitForResponse;
import pl.north93.zgame.skyblock.api.utils.Coords2D;

/**
 * Odpowiada za zarządzanie konkretną instancją hosta wysp.
 */
public interface IIslandHostManager
{
    // Zwraca ilość wysp na tym serwerze.
    Integer getIslands();

    Coords2D getFirstFreeLocation(String islandType);

    // Metoda wywoływana gdy wyspa ma zostać utworzona na tym serwerze.
    @DoNotWaitForResponse
    void islandAdded(IslandData islandData);

    // Metoda wywoływana gdy wyspa ma zostać usunięta.
    @DoNotWaitForResponse
    void islandRemoved(UUID islandId);

    // Metoda wywoływana gdy trzeba zaktualizować informacje o wyspie.
    @DoNotWaitForResponse
    void islandDataChanged(UUID islandId);
}
