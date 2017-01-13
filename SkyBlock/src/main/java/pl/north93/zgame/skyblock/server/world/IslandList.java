package pl.north93.zgame.skyblock.server.world;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.bukkit.Chunk;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.skyblock.api.utils.Coords2D;

public class IslandList
{
    private final ReentrantReadWriteLock lock;
    private final Map<Coords2D, Island>  byIslandCoords;
    private final Map<Coords2D, Island>  byChunkCoords;
    private final Map<UUID, Island>      byIslandId;

    public IslandList()
    {
        this.lock = new ReentrantReadWriteLock();
        this.byIslandCoords = new HashMap<>(512);
        this.byChunkCoords = new HashMap<>(512);
        this.byIslandId = new HashMap<>(512);
    }

    public void addIsland(final Island island)
    {
        try
        {
            this.lock.writeLock().lock();
            this.byIslandCoords.put(island.getIslandCoordinates(), island);
            for (final Coords2D coords2D : island.getLocation().getIslandChunks())
            {
                this.byChunkCoords.put(coords2D, island);
            }
            this.byIslandId.put(island.getId(), island);
        }
        finally
        {
            this.lock.writeLock().unlock();
        }
    }

    public void removeIsland(final Island island)
    {
        try
        {
            this.lock.writeLock().lock();
            this.byIslandCoords.remove(island.getIslandCoordinates());
            for (final Coords2D coords2D : island.getLocation().getIslandChunks())
            {
                this.byChunkCoords.remove(coords2D);
            }
            this.byIslandId.remove(island.getId());
        }
        finally
        {
            this.lock.writeLock().unlock();
        }
    }

    public int countIslands() // zwraca ilość wysp na tej liście/świecie.
    {
        try
        {
            this.lock.readLock().lock();
            return this.byIslandCoords.size();
        }
        finally
        {
            this.lock.readLock().unlock();
        }
    }

    public Island getByCoords(final Coords2D islandCoordinates)
    {
        try
        {
            this.lock.readLock().lock();
            return this.byIslandCoords.get(islandCoordinates);
        }
        finally
        {
            this.lock.readLock().unlock();
        }
    }

    public Island getById(final UUID islandId)
    {
        try
        {
            this.lock.readLock().lock();
            return this.byIslandId.get(islandId);
        }
        finally
        {
            this.lock.readLock().unlock();
        }
    }

    public Island getByChunk(final int x, final int z)
    {
        try
        {
            this.lock.readLock().lock();
            return this.byChunkCoords.get(new Coords2D(x, z));
        }
        finally
        {
            this.lock.readLock().unlock();
        }
    }

    public Island getByChunk(final Chunk chunk)
    {
        try
        {
            this.lock.readLock().lock();
            return this.getByChunk(chunk.getX(), chunk.getZ());
        }
        finally
        {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
