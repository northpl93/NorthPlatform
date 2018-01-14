package pl.north93.zgame.antycheat.utils;

import java.util.function.Consumer;

import net.minecraft.server.v1_10_R1.AxisAlignedBB;
import net.minecraft.server.v1_10_R1.IBlockData;
import net.minecraft.server.v1_10_R1.WorldServer;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.util.CraftMagicNumbers;

public class BlockUtils
{
    /**
     * Zwraca dokładny AABB bloku pobrany z NMS.
     *
     * @param block Blok dla którego pobieramy AABB.
     * @return AABB danego bloku.
     */
    public static AABB getExactBlockAABB(final Block block)
    {
        final net.minecraft.server.v1_10_R1.Block nmsBlock = CraftMagicNumbers.getBlock(block);
        final IBlockData nmsBlockData = nmsBlock.getBlockData();
        final WorldServer nmsWorld = ((CraftWorld) block.getWorld()).getHandle();

        final AxisAlignedBB nmsAabb = nmsBlockData.c(nmsWorld, null);
        if (nmsAabb == null)
        {
            return new AABB(0, 0, 0, 0, 0, 0);
        }
        return new AABB(nmsAabb, block.getX(), block.getY(), block.getZ());
    }

    /**
     * Sprawdza czy dany blok koliduje z danym AABB uwzględniając dokładny AABB danego bloku.
     * Ma to znaczenie dla np. płotków i innych niepełnych bloków.
     *
     * @param block Blok którego kolizje sprawdzamy.
     * @param aabb AABB którego kolizje sprawdzamy.
     * @return True jesli dokładny AABB bloku i AABB z argumentów kolidują.
     */
    public static boolean exactCollides(final Block block, final AABB aabb)
    {
        return getExactBlockAABB(block).intersects(aabb);
    }

    /**
     * Sprawdza czy dany AABB koliduje z danym {@link Material}.
     *
     * @param world
     * @param aabb
     * @param material
     * @return
     */
    public static boolean exactCollides(final World world, final AABB aabb, final Material material)
    {
        final CollidingBlocksIterator collidingBlocksIterator = new CollidingBlocksIterator(world, aabb);
        while (collidingBlocksIterator.hasNext())
        {
            final Block block = collidingBlocksIterator.next();
            if (block.getType() == material && exactCollides(block, aabb))
            {
                return true;
            }
        }

        return false;
    }

    public static void enumerateCollidingBlocks(final World world, final AABB aabb, final Consumer<Block> blockConsumer)
    {
        final CollidingBlocksIterator collidingBlocksIterator = new CollidingBlocksIterator(world, aabb);
        while (collidingBlocksIterator.hasNext())
        {
            blockConsumer.accept(collidingBlocksIterator.next());
        }
    }
}