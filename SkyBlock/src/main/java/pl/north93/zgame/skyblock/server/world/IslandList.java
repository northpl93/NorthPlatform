package pl.north93.zgame.skyblock.server.world;

import java.util.HashMap;
import java.util.Map;

import pl.north93.zgame.skyblock.api.utils.Coords2D;

class IslandList
{
    private final Map<Coords2D, Island> byIslandCoords;

    public IslandList()
    {
        this.byIslandCoords = new HashMap<>(512);
    }

    public void addIsland(final Island island)
    {
        this.byIslandCoords.put(island.getIslandData().getIslandLocation(), island);
    }

    public int countIslands() // zwraca ilość wysp na tej liście/świecie.
    {
        return this.byIslandCoords.size();
    }

    public Island getByCoords(final Coords2D islandCoordinates)
    {
        return this.byIslandCoords.get(islandCoordinates);
    }
}
