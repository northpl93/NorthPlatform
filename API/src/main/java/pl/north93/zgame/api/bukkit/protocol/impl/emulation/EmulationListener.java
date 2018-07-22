package pl.north93.zgame.api.bukkit.protocol.impl.emulation;

import static org.diorite.commons.reflections.DioriteReflectionUtils.getField;


import java.util.List;

import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.PacketPlayOutMapChunk;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import org.diorite.commons.reflections.FieldAccessor;

import lombok.extern.slf4j.Slf4j;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.protocol.PacketEvent;
import pl.north93.zgame.api.bukkit.protocol.PacketHandler;
import pl.north93.zgame.api.bukkit.server.event.ServerStartedEvent;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

@Slf4j
public class EmulationListener implements Listener
{
    private static final FieldAccessor<Integer> xField = getField(PacketPlayOutMapChunk.class, "a");
    private static final FieldAccessor<Integer> zField = getField(PacketPlayOutMapChunk.class, "b");
    private static final FieldAccessor<List<NBTTagCompound>> tileEntitiesField = getField(PacketPlayOutMapChunk.class, "e");

    @Inject
    private EmulationManager manager;

    @Bean
    private EmulationListener(final BukkitApiCore apiCore)
    {
        apiCore.registerEvents(this);
    }

    @PacketHandler
    public void chunkPacketInterceptor(final PacketEvent<PacketPlayOutMapChunk> event)
    {
        final PacketPlayOutMapChunk packet = event.getPacket();

        final Integer chunkX = xField.get(packet);
        final Integer chunkZ = zField.get(packet);
        final List<NBTTagCompound> tileEntities = tileEntitiesField.get(packet);

        final org.bukkit.Chunk chunk = event.getPlayer().getWorld().getChunkAt(chunkX, chunkZ);
        this.manager.getStorage(chunk).addCustomTileEntities(tileEntities);
    }

    @EventHandler
    public void processEmulatorsOnChunkLoad(final ChunkLoadEvent event)
    {
        this.scanChunk(event.getChunk());
    }

    @EventHandler
    public void processEmulatorsOnServerStart(final ServerStartedEvent event)
    {
        this.performInitialScan();
    }

    void scanChunk(final org.bukkit.Chunk chunk)
    {
        log.debug("Scanning chunk {}, {} for blocks needing 1.12->1.13 emulation", chunk.getX(), chunk.getZ());

        final CraftChunk craftChunk = (CraftChunk) chunk;
        final ChunkStorage storage = this.manager.getStorage(craftChunk);

        final Chunk nmsChunk = craftChunk.getHandle();
        final int[] heightMap = nmsChunk.heightMap;

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                for (int y = heightMap[z << 4 | x]; y > 0; y--)
                {
                    final Block block = craftChunk.getBlock(craftChunk.getX() * 16 + x, y, craftChunk.getZ() * 16 + z);

                    final BlockEmulator emulatorForType = this.manager.getEmulatorForType(block.getType());
                    if (emulatorForType == null || ! emulatorForType.isApplicable(block))
                    {
                        continue;
                    }

                    log.debug("Adding block emulator to {}", block);
                    storage.addEmulator(new BlockLocation(block.getX(), block.getY(), block.getZ()), emulatorForType);
                }
            }
        }
    }

    private void performInitialScan()
    {
        for (final World world : Bukkit.getWorlds())
        {
            for (final org.bukkit.Chunk chunk : world.getLoadedChunks())
            {
                this.scanChunk(chunk);
            }
        }
    }
}

class TestCmd extends NorthCommand // todo remove
{
    @Inject
    EmulationListener listener;

    public TestCmd()
    {
        super("scanchunk");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final org.bukkit.Chunk chunk = player.getLocation().getChunk();

        sender.sendMessage("Scanning {0}, {1}", chunk.getX(), chunk.getZ());
        this.listener.scanChunk(chunk);
    }
}