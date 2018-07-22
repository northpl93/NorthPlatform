package pl.north93.zgame.api.bukkit.protocol.impl.emulation;

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
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;

import lombok.extern.slf4j.Slf4j;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;

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

    // wymusza wywolanie emulatora tego bloku i rozgloszenie nowego tile entity
    public void updateDataNow(final Block block)
    {
        final CraftChunk chunk = (CraftChunk) block.getChunk();
        final ChunkStorage storage = this.getStorage(chunk);

        final BlockEmulator emulator = storage.getEmulator(new BlockLocation(block.getX(), block.getY(), block.getZ()));
        if (emulator == null)
        {
            return;
        }

        final WorldServer worldServer = (WorldServer) chunk.getHandle().world;
        final PlayerChunk playerChunk = worldServer.getPlayerChunkMap().getChunk(chunk.getX(), chunk.getZ());
        if (playerChunk == null)
        {
            return;
        }

        final BlockData data = emulator.getData(block);

        final NBTTagCompound compound = new NBTTagCompound();
        data.writeToNbt(compound);

        final PacketPlayOutTileEntityData packet = new PacketPlayOutTileEntityData(new BlockPosition(data.getX(), data.getY(), data.getZ()), 0, compound);
        for (final EntityPlayer entityPlayer : playerChunk.c) // players viewing this chunk
        {
            entityPlayer.playerConnection.sendPacket(packet);
        }
    }

    // próbuje dodac emulator do podanego bloku
    public boolean tryAddEmulatorTo(final Block block)
    {
        final BlockEmulator emulatorForType = this.getEmulatorForType(block.getType());
        if (emulatorForType == null || ! emulatorForType.isApplicable(block))
        {
            return false;
        }

        log.debug("Adding block emulator to {}", block);
        final ChunkStorage storage = this.getStorage(block.getChunk());
        storage.addEmulator(new BlockLocation(block.getX(), block.getY(), block.getZ()), emulatorForType);
        return true;
    }

    // usuwa emulator z podanego bloku
    public void removeEmulator(final Block block)
    {
        final ChunkStorage storage = this.getStorage(block.getChunk());
        storage.removeEmulator(new BlockLocation(block.getX(), block.getY(), block.getZ()));
    }

    // skanuje podany chunk i automatycznie rejestruje emulatory
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
                    this.tryAddEmulatorTo(block);
                }
            }
        }
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
}
