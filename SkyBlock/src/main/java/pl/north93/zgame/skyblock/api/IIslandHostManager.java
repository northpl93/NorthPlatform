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

    @DoNotWaitForResponse
    void recalculatePoints();

    // Wysyłane gdy gracz znajduje się na tym serwerze i trzeba
    // go przenieść na konkretną wyspę.
    @DoNotWaitForResponse
    void tpToIsland(UUID player, IslandData islandData);

    // Metoda wywoływana gdy wyspa ma zostać utworzona na tym serwerze.
    @DoNotWaitForResponse
    void islandAdded(UUID islandId, String islandType);

    // Metoda wywoływana gdy wyspa ma zostać usunięta.
    // Przesyłam tutaj IslandData ponieważ serwer nie mógłby wtedy pobrać tego z bazy
    @DoNotWaitForResponse
    void islandRemoved(IslandData islandData);

    // Wywoływane gdy na wyspie trzeba zmienić biom
    @DoNotWaitForResponse
    void biomeChanged(UUID islandId, String islandType, NorthBiome newBiome);
}
