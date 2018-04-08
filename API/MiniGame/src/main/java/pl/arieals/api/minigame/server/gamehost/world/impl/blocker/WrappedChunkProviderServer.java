package pl.arieals.api.minigame.server.gamehost.world.impl.blocker;

import javax.annotation.Nullable;

import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_12_R1.ChunkProviderServer;
import net.minecraft.server.v1_12_R1.IChunkLoader;

import org.diorite.commons.reflections.DioriteReflectionUtils;
import org.diorite.commons.reflections.FieldAccessor;

public class WrappedChunkProviderServer extends ChunkProviderServer
{
    private static FieldAccessor<IChunkLoader> chunkLoader = DioriteReflectionUtils.getField(ChunkProviderServer.class, "chunkLoader");

    public WrappedChunkProviderServer(final ChunkProviderServer old)
    {
        super(old.world, chunkLoader.get(old), old.chunkGenerator);
        this.chunks = old.chunks;
    }

    @Override
    public Chunk originalGetChunkAt(final int x, final int z)
    {
        final Chunk chunkIfLoaded = this.getLoadedChunkAt(x, z);
        if (chunkIfLoaded != null)
        {
            return chunkIfLoaded;
        }

        final DummyChunk chunk = new DummyChunk(this.world, x, z);
        this.chunks.put(ChunkCoordIntPair.a(x, z), chunk);
        chunk.addEntities();
        chunk.loadNearby(this, this.chunkGenerator, true);
        return chunk;
    }

    @Nullable
    @Override
    public Chunk originalGetOrLoadChunkAt(final int i, final int j)
    {
        return this.originalGetChunkAt(i, j);
    }

    @Override
    public void saveChunk(final Chunk chunk, boolean unloaded)
    {
        // do nothing
    }
}
