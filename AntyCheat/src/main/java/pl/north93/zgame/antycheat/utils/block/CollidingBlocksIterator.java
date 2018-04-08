package pl.north93.zgame.antycheat.utils.block;

import static org.diorite.commons.math.DioriteMathUtils.floor;


import java.util.Iterator;

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
    private final int   baseX, baseY, baseZ;
    private final int   sizeX, sizeY, sizeZ;
    private int x, y, z;

    public CollidingBlocksIterator(final World world, final AABB aabb)
    {
        this.world = world;
        this.baseX = floor(aabb.minX);
        this.baseY = floor(aabb.minY);
        this.baseZ = floor(aabb.minZ);
        this.sizeX = Math.abs(floor(aabb.maxX) - this.baseX) + 1;
        this.sizeY = Math.abs(floor(aabb.maxY) - this.baseY) + 1;
        this.sizeZ = Math.abs(floor(aabb.maxZ) - this.baseZ) + 1;
        this.x = this.y = this.z = 0;
    }

    @Override
    public boolean hasNext()
    {
        return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
    }

    @Override
    public Block next()
    {
        final Block block = this.world.getBlockAt(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);
        if (++ this.x >= this.sizeX)
        {
            this.x = 0;
            if (++ this.y >= this.sizeY)
            {
                this.y = 0;
                ++ this.z;
            }
        }
        return block;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("world", this.world).append("x", this.x).append("y", this.y).append("z", this.z).toString();
    }
}
