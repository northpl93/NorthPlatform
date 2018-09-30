package pl.north93.zgame.api.bukkit.emulation;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

import org.bukkit.Chunk;
import org.bukkit.block.Block;

/*default*/ class ChunkStorage
{
    private final WeakReference<Chunk> chunk;
    private final Map<BlockLocation, BlockData> blocks;

    public ChunkStorage(final Chunk chunk)
    {
        this.chunk = new WeakReference<>(chunk);
        this.blocks = new HashMap<>();
    }

    public void addData(final BlockData data)
    {
        final BlockLocation location = new BlockLocation(data.getX(), data.getY(), data.getZ());
        this.blocks.put(location, data);
    }

    public void removeData(final BlockLocation location)
    {
        this.blocks.remove(location);
    }

    public BlockData getData(final BlockLocation location)
    {
        return this.blocks.get(location);
    }

    public Chunk getChunk()
    {
        return this.chunk.get();
    }

    public void addCustomTileEntities(final List<NBTTagCompound> nbtList, final int bitmask)
    {
        final Chunk chunk = this.getChunk();

        final Iterator<Map.Entry<BlockLocation, BlockData>> iterator = this.blocks.entrySet().iterator();
        while (iterator.hasNext())
        {
            final Map.Entry<BlockLocation, BlockData> entry = iterator.next();
            final BlockLocation location = entry.getKey();

            final BlockData data = entry.getValue();
            if (this.isChunkSectionAbsent(bitmask, data))
            {
                // dana sekcja chunka nie jest zawarta w pakiecie wiec nie wysylamy tu tile entities
                continue;
            }

            final Block block = chunk.getBlock(location.getX(), location.getY(), location.getZ());
            if (! data.isStillValid(block))
            {
                // usuwamy niepoprawne bloki, aby nie spowodowac bledu w ViaVersion
                // który moze zapchac cala pamiec bungeecorda
                iterator.remove();
                continue;
            }

            final NBTTagCompound compound = new NBTTagCompound();
            //System.out.println("Writing to chunk NBT " + data);
            data.writeToNbt(compound);
            nbtList.add(compound);
        }
    }

    private boolean isChunkSectionAbsent(final int bitmask, final BlockData data)
    {
        return (bitmask & (1 << (data.getY() >> 4))) == 0;
    }
}
