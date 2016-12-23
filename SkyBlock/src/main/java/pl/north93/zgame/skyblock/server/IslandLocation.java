package pl.north93.zgame.skyblock.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class IslandLocation
{
    private static final int CHUNK_SIZE = 16;
    private final World world;
    private final int   centerChunkX, centerChunkZ;
    private final int   radius;

    public IslandLocation(final World world, final int centerChunkX, final int centerChunkZ, final int radius)
    {
        this.world = world;
        this.centerChunkX = centerChunkX;
        this.centerChunkZ = centerChunkZ;
        this.radius = radius;
    }

    public int getCenterChunkX()
    {
        return this.centerChunkX;
    }

    public int getCenterChunkZ()
    {
        return this.centerChunkZ;
    }

    public int getRadius()
    {
        return this.radius;
    }

    public Pair<Location, Location> getIslandCorners()
    {
        return this.cuboidCorners(this.centerChunkX, this.centerChunkZ, this.radius);
    }

    public Set<Chunk> getIslandChunks()
    {
        final Set<Chunk> chunks = new HashSet<>();
        final Pair<Location, Location> corners = this.getIslandCorners();

        final Chunk upperLeft = this.world.getChunkAt(corners.getLeft());
        final Chunk lowerRight = this.world.getChunkAt(corners.getRight());

        for (int x = upperLeft.getX(); x <= lowerRight.getX(); x++)
        {
            for (int z = lowerRight.getZ(); z <= upperLeft.getZ(); z++)
            {
                chunks.add(this.world.getChunkAt(x, z));
            }
        }

        return chunks;
    }

    public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2)
    {
        final List<Block> blocks = new ArrayList<>();

        final int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        final int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());

        final int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        final int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());

        final int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        final int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());

        for(int x = bottomBlockX; x <= topBlockX; x++)
        {
            for(int z = bottomBlockZ; z <= topBlockZ; z++)
            {
                for(int y = bottomBlockY; y <= topBlockY; y++)
                {
                    final Block block = loc1.getWorld().getBlockAt(x, y, z);

                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

    // down right. In chunk x0/y0 its x0/y0
    private Location chunkDRCorner(final int chunkX, final int chunkZ)
    {
        return new Location(this.world, chunkX * CHUNK_SIZE, 0, chunkZ * CHUNK_SIZE);
    }

    private Pair<Location, Location> cuboidCorners(final int centerChunkX, final int centerChunkZ, final int radius)
    {
        final Location upLeft = new Location(this.world, (centerChunkX * CHUNK_SIZE) + 7 + radius, 0, (centerChunkZ * CHUNK_SIZE) + 7 + radius);
        final Location downRight = new Location(this.world, (centerChunkX * CHUNK_SIZE) + 8 - radius, 0, (centerChunkZ * CHUNK_SIZE) + 8 - radius);

        return ImmutablePair.of(upLeft, downRight);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("world", this.world).append("centerChunkX", this.centerChunkX).append("centerChunkZ", this.centerChunkZ).append("radius", this.radius).toString();
    }
}
