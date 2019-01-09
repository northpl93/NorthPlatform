package pl.north93.northplatform.antycheat.utils.block;

import javax.annotation.Nullable;

import java.util.function.Consumer;

import net.minecraft.server.v1_12_R1.AxisAlignedBB;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.IBlockData;
import net.minecraft.server.v1_12_R1.WorldServer;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import pl.north93.northplatform.antycheat.utils.AABB;

public final class BlockUtils
{
    /**
     * Zwraca dokładny AABB bloku pobrany z NMS który blokuje gracza,
     * nie mozna przez niego przejsc. Jesli przez caly blok da sie przejsc to tu moze byc null.
     *
     * @param block Blok dla którego pobieramy AABB.
     * @return AABB danego bloku.
     */
    public static @Nullable
    AABB getExactBlockBlockingAABB(final Block block)
    {
        final WorldServer nmsWorld = ((CraftWorld) block.getWorld()).getHandle();

        final BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
        final IBlockData nmsBlockData = nmsWorld.getType(blockPosition);

        final AxisAlignedBB nmsAabb = nmsBlockData.d(nmsWorld, blockPosition);
        if (nmsAabb == null)
        {
            return null;
        }
        return new AABB(nmsAabb, block.getX(), block.getY(), block.getZ());
    }

    /**
     * Zwraca dokładny AABB bloku pobrany z NMS. Nie zawsze blokuje on gracza.
     *
     * @param block Blok dla którego pobieramy AABB.
     * @return AABB danego bloku.
     */
    public static AABB getExactBlockAABB(final Block block)
    {
        final WorldServer nmsWorld = ((CraftWorld) block.getWorld()).getHandle();

        final BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
        final IBlockData nmsBlockData = nmsWorld.getType(blockPosition);

        final AxisAlignedBB nmsAabb = nmsBlockData.e(nmsWorld, blockPosition);
        return new AABB(nmsAabb, block.getX(), block.getY(), block.getZ());
    }

    /**
     * Sprawdza czy dany blok koliduje z danym AABB uniemożliwiając przejście przez niego
     * uwzględniając dokładny AABB danego bloku. Ma to znaczenie dla np. płotków i innych niepełnych bloków.
     * Niektóre bloki jak woda tu zawsze zwrócą false bo przez nie można swobodnie przechodzić.
     *
     * @param block Blok którego kolizje sprawdzamy.
     * @param aabb AABB którego kolizje sprawdzamy.
     * @return True jesli dokładny AABB bloku i AABB z argumentów kolidują.
     */
    public static boolean exactBlockingCollides(final Block block, final AABB aabb)
    {
        final AABB exactBlockingBlockAABB = getExactBlockBlockingAABB(block);
        return exactBlockingBlockAABB != null && exactBlockingBlockAABB.intersects(aabb);
    }

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

    public static long getFlags(final World world, final AABB aabb)
    {
        long flags = 0;

        final CollidingBlocksIterator collidingBlocksIterator = new CollidingBlocksIterator(world, aabb);
        while (collidingBlocksIterator.hasNext())
        {
            final Block block = collidingBlocksIterator.next();
            if (block.getType() == Material.AIR)
            {
                continue;
            }

            flags |= BlockFlag.getFlags(block.getType());
        }

        return flags;
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