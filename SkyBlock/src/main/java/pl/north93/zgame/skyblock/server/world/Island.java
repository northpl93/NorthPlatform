package pl.north93.zgame.skyblock.server.world;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.API;
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

    public void updateIslandData(final IslandData islandData)
    {
        this.islandData.setOwnerId(islandData.getOwnerId());
        this.islandData.setName(islandData.getName());
        this.islandData.setHomeLocation(islandData.getHomeLocation());
    }

    public IslandLocation getLocation()
    {
        return this.location;
    }

    // calculate home location.
    public Location getHomeLocation()
    {
        return this.location.fromRelative(this.islandData.getHomeLocation());
    }

    public void setHomeLocation(final Location location)
    {
        this.islandData.setHomeLocation(this.location.toRelative(location));
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
        final Pair<Location, Location> corners = location.getIslandCorners();
        final Location first = corners.getLeft();
        first.setY(5);
        final Location right = corners.getRight();
        right.setY(5);
        ((BukkitApiCore) API.getApiCore()).sync(() ->
        {
            IslandLocation.blocksFromTwoPoints(first, right).forEach(block -> block.setType(Material.WOOL));
        });
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
