package pl.north93.zgame.skyblock.server.world;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import pl.north93.zgame.skyblock.api.utils.Coords2D;

public class IslandList
{
    private final Map<Coords2D, Island> byIslandCoords;
    private final Map<UUID, Island>     byIslandId;

    public IslandList()
    {
        this.byIslandCoords = new HashMap<>(512);
        this.byIslandId = new HashMap<>(512);
    }

    public void addIsland(final Island island)
    {
        this.byIslandCoords.put(island.getIslandData().getIslandLocation(), island);
        this.byIslandId.put(island.getIslandData().getIslandId(), island);
    }

    public void removeIsland(final Island island)
    {
        this.byIslandCoords.remove(island.getIslandData().getIslandLocation());
        this.byIslandId.remove(island.getIslandData().getIslandId());
    }

    public int countIslands() // zwraca ilość wysp na tej liście/świecie.
    {
        return this.byIslandCoords.size();
    }

    public Island getByCoords(final Coords2D islandCoordinates)
    {
        return this.byIslandCoords.get(islandCoordinates);
    }

    public Island getById(final UUID islandId)
    {
        return this.byIslandId.get(islandId);
    }
}
