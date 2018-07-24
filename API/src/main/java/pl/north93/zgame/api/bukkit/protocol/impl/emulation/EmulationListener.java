package pl.north93.zgame.api.bukkit.protocol.impl.emulation;

import static org.diorite.commons.reflections.DioriteReflectionUtils.getField;


import java.util.List;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.PacketPlayOutMapChunk;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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
    private static final FieldAccessor<Integer> bitMaskField = getField(PacketPlayOutMapChunk.class, "c");
    private static final FieldAccessor<List<NBTTagCompound>> tileEntitiesField = getField(PacketPlayOutMapChunk.class, "e");

    @Inject
    private EmulationManager manager;

    @Bean
    private EmulationListener(final BukkitApiCore apiCore)
    {
        apiCore.registerEvents(this);
    }

    @SuppressWarnings("ConstantConditions")
    @PacketHandler
    public void chunkPacketInterceptor(final PacketEvent<PacketPlayOutMapChunk> event)
    {
        final PacketPlayOutMapChunk packet = event.getPacket();

        final int chunkX = xField.get(packet);
        final int chunkZ = zField.get(packet);
        final int bitmask = bitMaskField.get(packet); // bitmaska zawierajaca informacje o wysylanych sekcjach chunka
        final List<NBTTagCompound> tileEntities = tileEntitiesField.get(packet);

        final World world = event.getPlayer().getWorld();
        if (! world.isChunkLoaded(chunkX, chunkZ))
        {
            log.warn("Chunk isn't loaded when sending to player {}, {}", chunkX, chunkZ);
            return;
        }

        final Chunk chunk = world.getChunkAt(chunkX, chunkZ);
        this.manager.getStorage(chunk).addCustomTileEntities(tileEntities, bitmask);
    }

    @EventHandler
    public void processEmulatorsOnChunkLoad(final ChunkLoadEvent event)
    {
        this.manager.scanChunk(event.getChunk());
    }

    @EventHandler
    public void processEmulatorsOnServerStart(final ServerStartedEvent event)
    {
        this.performInitialScan();
    }

    @EventHandler
    public void registerEmulatorOnBlockPlace(final BlockPlaceEvent event)
    {
        final Block block = event.getBlock();
        if (this.manager.tryAddEmulatorTo(block))
        {
            this.manager.updateDataNow(block);
        }
    }

    @EventHandler
    public void removeEmulatorOnBlockDestroy(final BlockBreakEvent event)
    {
        final Block block = event.getBlock();
        this.manager.removeEmulator(block);
    }

    private void performInitialScan()
    {
        for (final World world : Bukkit.getWorlds())
        {
            for (final Chunk chunk : world.getLoadedChunks())
            {
                this.manager.scanChunk(chunk);
            }
        }
    }
}

class TestCmd extends NorthCommand // todo remove
{
    @Inject
    EmulationManager manager;

    public TestCmd()
    {
        super("scanchunk");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final Chunk chunk = player.getLocation().getChunk();

        sender.sendMessage("Scanning {0}, {1}", chunk.getX(), chunk.getZ());
        this.manager.scanChunk(chunk);
    }
}