package pl.north93.zgame.skyblock.server.world.points;

import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.server.v1_10_R1.Chunk;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.example.MappedFaweQueue;
import com.boydti.fawe.object.visitor.FastChunkIterator;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.bukkit.BukkitWorld;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_10_R1.CraftChunk;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.api.IslandData;
import pl.north93.zgame.skyblock.server.world.Island;
import pl.north93.zgame.skyblock.server.world.IslandLocation;

public class IslandPoints
{
    private final Object            sync;
    private final PointsHelper      pointsHelper;
    private final Island            island;
    private double  currentPoints;
    private boolean needsPersist;
    private volatile boolean isRecalculating;

    public IslandPoints(final PointsHelper pointsHelper, final Island island, final Value<IslandData> islandData)
    {
        this.sync = new Object();
        this.pointsHelper = pointsHelper;
        this.island = island;
        this.currentPoints = islandData.get().getPoints();
    }

    public void blockAdded(final Material material, final byte data)
    {
        synchronized (this.sync)
        {
            if (this.isRecalculating)
            {
                return;
            }
            this.needsPersist = true;
            this.currentPoints += this.pointsHelper.getBlockPrice(material, data);
        }
    }

    public void blockRemoved(final Material material, final byte data)
    {
        synchronized (this.sync)
        {
            if (this.isRecalculating)
            {
                return;
            }
            this.needsPersist = true;
            this.currentPoints -= this.pointsHelper.getBlockPrice(material, data);
        }
    }

    public void persist()
    {
        synchronized (this.sync)
        {
            if (! this.needsPersist || this.isRecalculating)
            {
                return;
            }
            this.pointsHelper.persist(this.island.getId(), this.currentPoints);
            this.needsPersist = false;
        }
    }

    public void recalculate()
    {
        try
        {
            synchronized (this.sync)
            {
                if (this.isRecalculating)
                {
                    return; // recalculating already started?
                }
                this.isRecalculating = true;
            }

            final long start = System.currentTimeMillis();
            this.currentPoints = 0;
            final IslandLocation location = this.island.getLocation();

            final Set<Vector2D> chunks = location.getIslandChunks().stream().map(c2d -> new Vector2D(c2d.getX(), c2d.getZ())).collect(Collectors.toSet());

            final MappedFaweQueue queue = (MappedFaweQueue) FaweAPI.createQueue(new BukkitWorld(location.getWorld()), true);
            for (final Vector2D vector2D : new FastChunkIterator(chunks, queue))
            {
                final int chunkX = (int) vector2D.getX();
                final int chunkZ = (int) vector2D.getZ();
                final Chunk nmsChunk = (Chunk) queue.ensureChunkLoaded(chunkX, chunkZ);
                if (nmsChunk == null)
                {
                    System.out.println("WARNING! nmsChunk is null at " + chunkX + "/" + chunkZ + " islandId:" + this.island.getId());
                    continue;
                }
                final CraftChunk craftChunk = (CraftChunk) nmsChunk.bukkitChunk;
                final int[] heightMap = nmsChunk.heightMap;

                for (int x = 0; x < 16; x++)
                {
                    for (int z = 0; z < 16; z++)
                    {
                        for (int i = heightMap[z << 4 | x]; i >= 0; i--)
                        {
                            final Block block = craftChunk.getBlock((chunkX << 4) + x, i, (chunkZ << 4) + z);
                            final Material type = block.getType();
                            if (type == Material.AIR)
                            {
                                continue;
                            }
                            this.currentPoints += this.pointsHelper.getBlockPrice(type, (short) block.getData());
                        }
                    }
                }
            }

            final long end = System.currentTimeMillis() - start;
            System.out.println("Recalculated island " + this.island.getId() + " in " + end + "ms!");
        }
        finally
        {
            synchronized (this.sync)
            {
                this.isRecalculating = false;
                this.needsPersist = true;
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("currentPoints", this.currentPoints).append("needsPersist", this.needsPersist).toString();
    }
}
