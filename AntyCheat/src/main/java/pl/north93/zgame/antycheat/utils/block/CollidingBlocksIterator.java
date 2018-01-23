package pl.north93.zgame.antycheat.utils.block;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.antycheat.utils.AABB;

/**
 * Iteruje po wszystkich blokach kolidujących z danym AABB.
 *
 * UWAGA! Bloki są traktowane jako kostki 1x1x1, należy użyć dodatkowej
 * metody porównującej AABB bloku jeśli chcesz być dokładny (np płotek).
 */
public final class CollidingBlocksIterator implements Iterator<Block>
{
    private final World world;
    private final int   maxX, maxY, maxZ;
    private boolean done;
    private int x, y, z;

    public CollidingBlocksIterator(final World world, final AABB aabb)
    {
        this.world = world;
        this.x = Location.locToBlock(aabb.minX);
        this.y = Location.locToBlock(aabb.minY);
        this.z = Location.locToBlock(aabb.minZ);
        this.maxX = Location.locToBlock(aabb.maxX);
        this.maxY = Location.locToBlock(aabb.maxY);
        this.maxZ = Location.locToBlock(aabb.maxZ);
    }

    @Override
    public boolean hasNext()
    {
        return ! this.done;
    }

    @Override
    public Block next()
    {
        final Block block = this.world.getBlockAt(this.x, this.y, this.z);
        if (this.x < this.maxX)
        {
            this.x++;
        }
        else if (this.y < this.maxY)
        {
            this.y++;
        }
        else if (this.z < this.maxZ)
        {
            this.z++;
        }
        else
        {
            this.done = true;
        }
        return block;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("world", this.world).toString();
    }
}
