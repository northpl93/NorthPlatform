package pl.north93.zgame.skyblock.server.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import pl.north93.zgame.skyblock.api.utils.Coords2D;
import pl.north93.zgame.skyblock.api.utils.Coords3D;

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

    public World getWorld()
    {
        return this.world;
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

    /**
     * Zwraca dwa przeciwne narożniki tej wyspy.
     * Wysokość NIE jest ustalana.
     *
     * @return narożniki wyspy.
     */
    public Pair<Location, Location> getIslandCorners()
    {
        return this.cuboidCorners(this.centerChunkX, this.centerChunkZ, this.radius);
    }

    /**
     * Zwraca ilość chunków składających się na tę wyspę.
     *
     * @return Zbiór chunków.
     */
    public Set<Coords2D> getIslandChunks()
    {
        final Set<Coords2D> chunks = new HashSet<>();
        final Pair<Location, Location> corners = this.getIslandCorners();

        final Coords2D upperLeft = new Coords2D(corners.getLeft().getBlockX() >> 4, corners.getLeft().getBlockZ() >> 4);
        final Coords2D lowerRight = new Coords2D(corners.getRight().getBlockX() >> 4, corners.getRight().getBlockZ() >> 4);

        for (int x = lowerRight.getX(); x <= upperLeft.getX(); x++)
        {
            for (int z = lowerRight.getZ(); z <= upperLeft.getZ(); z++)
            {
                chunks.add(new Coords2D(x, z));
            }
        }

        return chunks;
    }

    /**
     * Zwraca ilość chunków zajmowaną przez tą wyspę.
     *
     * @return zajmowana ilość chunków
     */
    public int chunksCount()
    {
        return this.getIslandChunks().size(); // todo better implementation
    }

    public static List<Block> blocksFromTwoPoints(final Location loc1, final Location loc2)
    {
        final List<Block> blocks = new ArrayList<>();

        final int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        final int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());

        final int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        final int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());

        final int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        final int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());

        for (int x = bottomBlockX; x <= topBlockX; x++)
        {
            for (int z = bottomBlockZ; z <= topBlockZ; z++)
            {
                for (int y = bottomBlockY; y <= topBlockY; y++)
                {
                    final Block block = loc1.getWorld().getBlockAt(x, y, z);

                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

    public boolean isInside(final Location location)
    {
        final int locX = location.getBlockX();
        final int locZ = location.getBlockZ();

        final Pair<Location, Location> corners = this.getIslandCorners();
        return (locX >= corners.getRight().getBlockX() && locX <= corners.getLeft().getBlockX()) &&
                       (locZ >= corners.getRight().getBlockZ() && locZ <= corners.getLeft().getBlockZ());
    }

    public Coords3D toRelative(final Location location)
    {
        final Location drc = this.chunkDRCorner(this.centerChunkX, this.centerChunkZ);
        final int x = (int) (location.getX() - drc.getX());
        final int y = (int) location.getY();
        final int z = (int) (location.getZ() - drc.getZ());

        return new Coords3D(x, y, z);
    }

    public Location fromRelative(final Coords3D coords3D)
    {
        return this.fromRelative(coords3D.getX(), coords3D.getY(), coords3D.getZ());
    }

    public Location fromRelative(final double x, final double y, final double z)
    {
        final Location drc = this.chunkDRCorner(this.centerChunkX, this.centerChunkZ);
        final double relx = drc.getX() + x;
        final double relz = drc.getZ() + z;

        return new Location(this.world, relx, y, relz);
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
