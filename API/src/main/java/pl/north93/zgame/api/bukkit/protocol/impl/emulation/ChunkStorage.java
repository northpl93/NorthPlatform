package pl.north93.zgame.api.bukkit.protocol.impl.emulation;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

import org.bukkit.Chunk;
import org.bukkit.block.Block;

/*default*/ class ChunkStorage
{
    private final WeakReference<Chunk> chunk;
    private final Map<BlockLocation, BlockEmulator> blocks;

    public ChunkStorage(final Chunk chunk)
    {
        this.chunk = new WeakReference<>(chunk);
        this.blocks = new HashMap<>();
    }

    public void addEmulator(final BlockLocation location, final BlockEmulator emulator)
    {
        this.blocks.put(location, emulator);
    }

    public Chunk getChunk()
    {
        return this.chunk.get();
    }

    public void addCustomTileEntities(final List<NBTTagCompound> nbtList)
    {
        final Chunk chunk = this.getChunk();
        for (final Map.Entry<BlockLocation, BlockEmulator> entry : this.blocks.entrySet())
        {
            final BlockLocation location = entry.getKey();
            final Block block = chunk.getBlock(location.getX(), location.getY(), location.getZ());

            final BlockData data = entry.getValue().getData(block);

            final NBTTagCompound compound = new NBTTagCompound();
            //System.out.println("Writing to chunk NBT " + data);
            data.writeToNbt(compound);
            nbtList.add(compound);
        }
    }
}
