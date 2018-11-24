package pl.north93.northplatform.api.bukkit.emulation;

import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.PacketPlayOutTileEntityData;
import net.minecraft.server.v1_12_R1.PlayerChunk;
import net.minecraft.server.v1_12_R1.WorldServer;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.annotations.bean.Aggregator;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;

@Slf4j
/*default*/ class EmulationManager
{
    private final Map<Material, BlockEmulator> emulators = new HashMap<>();
    private final Map<Chunk, ChunkStorage> chunks = new WeakHashMap<>();

    @Bean
    private EmulationManager()
    {
    }

    @Aggregator(BlockEmulator.class)
    public void registerEmulator(final BlockEmulator emulator)
    {
        log.info("Registering 1.12->1.13 block emulator {}", emulator.getType());
        this.emulators.put(emulator.getType(), emulator);
    }

    @Nullable
    public BlockEmulator getEmulatorForType(final Material material)
    {
        return this.emulators.get(material);
    }

    public ChunkStorage getStorage(final Chunk chunk)
    {
        return this.chunks.computeIfAbsent(chunk, ChunkStorage::new);
    }

    // generuje dane dla danego bloku i pobliskich bloków
    public void generateDataFor(final Block block)
    {
        final BlockData blockData = this.generateDataFor0(block);
        if (blockData == null)
        {
            return;
        }

        // NORTH, EAST, SOUTH, WEST, UP, DOWN
        for (int i = 0; i < 6; i++)
        {
            final BlockFace blockFace = BlockFace.values()[i];
            this.generateDataFor0(block.getRelative(blockFace));
        }

        this.updateDataNow(block.getChunk(), blockData);
    }

    // usuwa emulator z podanego bloku
    public void removeEmulator(final Block block)
    {
        final ChunkStorage storage = this.getStorage(block.getChunk());
        storage.removeData(new BlockLocation(block.getX(), block.getY(), block.getZ()));
    }

    // skanuje podany chunk i automatycznie generuje dane bloków
    public void scanChunk(final org.bukkit.Chunk chunk)
    {
        log.debug("Scanning chunk {}, {} for blocks needing 1.12->1.13 emulation", chunk.getX(), chunk.getZ());

        final CraftChunk craftChunk = (CraftChunk) chunk;
        final net.minecraft.server.v1_12_R1.Chunk nmsChunk = craftChunk.getHandle();

        final int[] heightMap = nmsChunk.heightMap;
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                for (int y = heightMap[z << 4 | x]; y > 0; y--)
                {
                    final Block block = craftChunk.getBlock(craftChunk.getX() * 16 + x, y, craftChunk.getZ() * 16 + z);
                    this.generateDataFor0(block);
                }
            }
        }
    }

    // próbuje wygenerowac i dodac BlockData dla danego bloku
    private BlockData generateDataFor0(final Block block)
    {
        final BlockEmulator emulatorForType = this.getEmulatorForType(block.getType());
        if (emulatorForType == null || ! emulatorForType.isApplicable(block))
        {
            return null;
        }

        log.debug("Generating emulated data for block {}", block);
        final ChunkStorage storage = this.getStorage(block.getChunk());

        final BlockData data = emulatorForType.getData(block);
        storage.addData(data);
        return data;
    }

    // wymusza rozgloszenie nowego tile entity na podstawie BlockData
    private void updateDataNow(final Chunk chunk, final BlockData data)
    {
        final CraftChunk craftChunk = (CraftChunk) chunk;

        final WorldServer worldServer = (WorldServer) craftChunk.getHandle().world;
        final PlayerChunk playerChunk = worldServer.getPlayerChunkMap().getChunk(chunk.getX(), chunk.getZ());
        if (playerChunk == null)
        {
            return;
        }

        final NBTTagCompound compound = new NBTTagCompound();
        data.writeToNbt(compound);

        final PacketPlayOutTileEntityData packet = new PacketPlayOutTileEntityData(new BlockPosition(data.getX(), data.getY(), data.getZ()), 0, compound);
        for (final EntityPlayer entityPlayer : playerChunk.c) // players viewing this chunk
        {
            entityPlayer.playerConnection.sendPacket(packet);
        }
    }
}
