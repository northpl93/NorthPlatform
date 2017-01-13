package pl.north93.zgame.skyblock.server.world;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.api.IslandDao;
import pl.north93.zgame.skyblock.api.IslandData;
import pl.north93.zgame.skyblock.api.utils.Coords2D;

/**
 * Reprezentuje wyspę znajdującą się na konkretnym serwerze i świecie.
 */
public class Island
{
    private final IslandDao         islandDao;
    private final Value<IslandData> islandData;
    private final UUID              islandId;
    private final Coords2D          coordinates;
    private final IslandLocation    location;

    public Island(final IslandDao islandDao, final Value<IslandData> islandData, final IslandLocation location)
    {
        this.islandDao = islandDao;
        this.islandData = islandData;
        this.location = location;
        final IslandData cacheData = islandData.get();
        this.islandId = cacheData.getIslandId();
        this.coordinates = cacheData.getIslandLocation();
    }

    public UUID getId()
    {
        return this.islandId; // it's cached because islandData can be null while deleting island.
    }

    public Coords2D getIslandCoordinates()
    {
        return this.coordinates; // it's cached because islandData can be null while deleting island.
    }

    public IslandLocation getLocation()
    {
        return this.location;
    }

    // calculate home location.
    public Location getHomeLocation()
    {
        return this.location.fromRelative(this.islandData.get().getHomeLocation());
    }

    public void setHomeLocation(final Location location)
    {
        this.updateData(data ->
        {
            data.setHomeLocation(this.location.toRelative(location));
        });
    }

    public boolean canBuild(final UUID uuid)
    {
        final IslandData islandData = this.islandData.get();
        return islandData.getOwnerId().equals(uuid) || islandData.getMembersUuid().contains(uuid);
    }

    public List<Player> getPlayersInIsland()
    {
        return Bukkit.getOnlinePlayers()
                     .stream()
                     .filter(p -> p.getWorld().equals(this.getLocation().getWorld()) && this.location.isInside(p.getLocation()))
                     .collect(Collectors.toList());
    }

    /**
     * Clears area obtained by this island.
     */
    public void clear()
    {
        final World world = this.location.getWorld();
        for (final Coords2D coords : this.location.getIslandChunks())
        {
            world.regenerateChunk(coords.getX(), coords.getZ());
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

    private void updateData(final Consumer<IslandData> updater)
    {
        this.islandData.update(data ->
        {
            updater.accept(data);
            this.islandDao.saveIsland(data);
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("islandData", this.islandData).append("location", this.location).toString();
    }
}
