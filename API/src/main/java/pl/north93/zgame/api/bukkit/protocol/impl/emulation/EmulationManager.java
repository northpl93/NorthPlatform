package pl.north93.zgame.api.bukkit.protocol.impl.emulation;

import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.Chunk;
import org.bukkit.Material;

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
