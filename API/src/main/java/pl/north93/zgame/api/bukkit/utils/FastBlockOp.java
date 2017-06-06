package pl.north93.zgame.api.bukkit.utils;

import net.minecraft.server.v1_10_R1.Chunk;
import net.minecraft.server.v1_10_R1.ChunkSection;
import net.minecraft.server.v1_10_R1.IBlockData;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_10_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_10_R1.util.CraftMagicNumbers;

public class FastBlockOp
{
    /**
     * Block#setType jest bardzo wolne.
     * Ta metoda omija caly smietnik zwiazany z obliczaniem fizyki, swiatla itp.
     *
     * @param block blok ktory zmienic.
     * @param material material nowego bloku.
     * @param data data nowego bloku.
     */
    public static void setType(final Block block, final Material material, final byte data)
    {
        final Chunk nmsChunk = ((CraftChunk) block.getChunk()).getHandle();
        final ChunkSection[] sections = nmsChunk.getSections();

        final ChunkSection chunksection = sections[block.getY() >> 4];

        final IBlockData newBlockData = CraftMagicNumbers.getBlock(material).fromLegacyData(data);
        // ta metoda ma inne parametry w normalnym spigocie!!! NorthSpigot ma dodatkowy swiat i chunk
        chunksection.setType(nmsChunk.world, nmsChunk, block.getX() & 15, block.getY() & 15, block.getZ() & 15, newBlockData);
    }
}
