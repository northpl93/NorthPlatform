package pl.north93.zgame.skyblock.server.world;

import static pl.north93.zgame.skyblock.server.BiomeMapper.toBukkit;


import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.minecraft.server.v1_10_R1.BiomeBase;
import net.minecraft.server.v1_10_R1.Chunk;
import net.minecraft.server.v1_10_R1.PacketPlayOutMapChunk;
import net.minecraft.server.v1_10_R1.PlayerConnection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_10_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_10_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.api.HomeLocation;
import pl.north93.zgame.skyblock.api.IslandDao;
import pl.north93.zgame.skyblock.api.IslandData;
import pl.north93.zgame.skyblock.api.NorthBiome;
import pl.north93.zgame.skyblock.api.utils.Coords2D;
import pl.north93.zgame.skyblock.api.utils.Coords3D;

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
        final HomeLocation home = this.islandData.get().getHomeLocation();
        final Location location = this.location.fromRelative(home.getX(), home.getY(), home.getZ());
        location.setYaw(home.getYaw());
        location.setPitch(home.getPitch());
        return location;
    }

    public void setHomeLocation(final Location location)
    {
        this.updateData(data ->
        {
            final Coords3D coords3D = this.location.toRelative(location);
            final HomeLocation home = new HomeLocation(coords3D.getX(), coords3D.getY(), coords3D.getZ(), location.getYaw(), location.getPitch());
            data.setHomeLocation(home);
        });
    }

    public boolean canBuild(final UUID uuid)
    {
        final IslandData islandData = this.islandData.get();
        return islandData.getOwnerId().equals(uuid) || islandData.getMembersUuid().contains(uuid);
    }

    public boolean isAcceptingVisits()
    {
        return this.islandData.get().getAcceptingVisits();
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

    public void buildWhiteWoolMarker()
    {
        final Pair<Location, Location> corners = this.location.getIslandCorners();
        final Location first = corners.getLeft();
        first.setY(1);
        final Location right = corners.getRight();
        right.setY(1);
        ((BukkitApiCore) API.getApiCore()).sync(() ->
        {
            IslandLocation.blocksFromTwoPoints(first, right).forEach(block -> block.setType(Material.WOOL));
        });
    }

    /**
     * This method only applies changes to island.
     *
     * @see pl.north93.zgame.skyblock.api.ISkyBlockManager#changeBiome(UUID, NorthBiome)
     * @param newBiome new biome to apply.
     */
    public void setBiome(final NorthBiome newBiome)
    {
        final List<Player> playersInIsland = this.getPlayersInIsland();
        for (final Coords2D chunkCoords : this.location.getIslandChunks())
        {
            // CraftWorld#setBiome(int, int, Biome)
            final CraftChunk craftChunk = (CraftChunk) this.location.getWorld().getChunkAt(chunkCoords.getX(), chunkCoords.getZ());
            final Chunk nmsChunk = craftChunk.getHandle();
            final byte biomeId = (byte) BiomeBase.REGISTRY_ID.a(CraftBlock.biomeToBiomeBase(toBukkit(newBiome)));

            Arrays.fill(nmsChunk.getBiomeIndex(), biomeId);

            for (final Player player : playersInIsland)
            {
                final CraftPlayer craftPlayer = (CraftPlayer) player;
                final PlayerConnection playerConnection = craftPlayer.getHandle().playerConnection;
                playerConnection.sendPacket(new PacketPlayOutMapChunk(nmsChunk, '\uffff'));
            }
        }
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
