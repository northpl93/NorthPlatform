package pl.north93.zgame.api.bukkit.utils.nms;

import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.ChunkSection;
import net.minecraft.server.v1_12_R1.IBlockData;
import net.minecraft.server.v1_12_R1.PlayerChunk;
import net.minecraft.server.v1_12_R1.WorldServer;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;

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

        final int relX = block.getX() & 15;
        final int relY = block.getY() & 15;
        final int relZ = block.getZ() & 15;

        final IBlockData newBlockData = CraftMagicNumbers.getBlock(material).fromLegacyData(data);
        // ta metoda ma inne parametry w normalnym spigocie!!! NorthSpigot ma dodatkowy swiat i chunk
        //chunksection.setType(nmsChunk.world, nmsChunk, relX, relY, relZ, newBlockData);
        chunksection.setType(relX, relY, relZ, newBlockData); // todo gdy zaaplikuje do northspigota patcha z eventem od modyfikacji bloku to trzeba poprawic

        final int mapIndex = relZ << 4 | relX;
        final int actual = nmsChunk.heightMap[mapIndex];
        if (block.getY() >= actual && material == Material.AIR)
        {
            fixHeightMap(block, nmsChunk, mapIndex);
        }
        else if (block.getY() > actual && material != Material.AIR)
        {
            nmsChunk.heightMap[mapIndex] = block.getY(); // todo check is it works
        }

        // informujemy o zmianie
        final PlayerChunk chunk = ((WorldServer) nmsChunk.world).getPlayerChunkMap().getChunk(nmsChunk.locX, nmsChunk.locZ);
        if (chunk != null)
        {
            // wywolanie metody a mozna znalezc w PlayerChunkMap#flagDirty(BlockPosition blockposition)
            // dodaje ona blok do listy dirty-blockow
            chunk.a(relX, block.getY(), relZ);
        }
    }

    private static void fixHeightMap(final Block block, final Chunk nmsChunk, final int mapIndex)
    {
        for (int i = block.getY(); i >= 0; i--)
        {
            if (! block.getChunk().getBlock(block.getX(), i, block.getZ()).isEmpty())
            {
                nmsChunk.heightMap[mapIndex] = i;
                return;
            }
        }
        nmsChunk.heightMap[mapIndex] = 0;
    }
}
