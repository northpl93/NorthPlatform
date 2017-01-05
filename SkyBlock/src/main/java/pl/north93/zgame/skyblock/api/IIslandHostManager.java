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

    // Wysyłane gdy gracz znajduje się na tym serwerze i trzeba
    // go przenieść na konkretną wyspę.
    @DoNotWaitForResponse
    void tpToIsland(UUID player, IslandData islandData);

    // Metoda wywoływana gdy wyspa ma zostać utworzona na tym serwerze.
    @DoNotWaitForResponse
    void islandAdded(IslandData islandData);

    // Metoda wywoływana gdy wyspa ma zostać usunięta.
    @DoNotWaitForResponse
    void islandRemoved(IslandData islandData);

    // Metoda wywoływana gdy trzeba zaktualizować informacje o wyspie.
    @DoNotWaitForResponse
    void islandDataChanged(IslandData islandData);
}
