package pl.north93.zgame.skyblock.server.world;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Chunk;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.skyblock.api.utils.Coords2D;

public class IslandList
{
    private final Map<Coords2D, Island> byIslandCoords;
    private final Map<Coords2D, Island> byChunkCoords;
    private final Map<UUID, Island>     byIslandId;

    public IslandList()
    {
        this.byIslandCoords = new HashMap<>(512);
        this.byChunkCoords = new HashMap<>(512);
        this.byIslandId = new HashMap<>(512);
    }

    public void addIsland(final Island island)
    {
        this.byIslandCoords.put(island.getIslandData().getIslandLocation(), island);
        for (final Chunk chunk : island.getLocation().getIslandChunks())
        {
            this.byChunkCoords.put(new Coords2D(chunk.getX(), chunk.getZ()), island);
        }
        this.byIslandId.put(island.getIslandData().getIslandId(), island);
    }

    public void removeIsland(final Island island)
    {
        this.byIslandCoords.remove(island.getIslandData().getIslandLocation());
        for (final Chunk chunk : island.getLocation().getIslandChunks())
        {
            this.byChunkCoords.remove(new Coords2D(chunk.getX(), chunk.getZ()));
        }
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

    public Island getByChunk(final int x, final int z)
    {
        return this.byChunkCoords.get(new Coords2D(x, z));
    }

    public Island getByChunk(final Chunk chunk)
    {
        return this.getByChunk(chunk.getX(), chunk.getZ());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
