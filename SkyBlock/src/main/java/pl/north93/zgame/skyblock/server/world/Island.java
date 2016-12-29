package pl.north93.zgame.skyblock.server.world;

import org.bukkit.Chunk;
import org.bukkit.World;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.skyblock.api.IslandData;

/**
 * Reprezentuje wyspę znajdującą się na konkretnym serwerze i świecie.
 */
public class Island
{
    private final IslandData     islandData;
    private final IslandLocation location;

    public Island(final IslandData islandData, final IslandLocation location)
    {
        this.islandData = islandData;
        this.location = location;
    }

    public IslandData getIslandData()
    {
        return this.islandData;
    }

    public IslandLocation getLocation()
    {
        return this.location;
    }

    /**
     * Clears area obtained by this island.
     */
    public void clear()
    {
        final World world = this.location.getWorld();
        for (final Chunk chunk : this.location.getIslandChunks())
        {
            world.regenerateChunk(chunk.getX(), chunk.getZ());
        }
    }

    /**
     * Pastes schematic on island.
     */
    public void loadSchematic()
    {
        // TODO
    }

    /**
     * Resets this island.
     */
    public void reset()
    {
        this.clear();
        this.loadSchematic();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("islandData", this.islandData).append("location", this.location).toString();
    }
}
