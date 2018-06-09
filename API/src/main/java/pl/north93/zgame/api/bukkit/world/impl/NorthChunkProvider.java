package pl.north93.zgame.api.bukkit.world.impl;

import java.lang.invoke.MethodHandle;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_12_R1.ChunkProviderServer;
import net.minecraft.server.v1_12_R1.IChunkLoader;
import net.minecraft.server.v1_12_R1.World;
import net.minecraft.server.v1_12_R1.WorldServer;

import pl.north93.zgame.api.global.utils.lang.CatchException;
import pl.north93.zgame.api.global.utils.lang.MethodHandlesUtils;
import pl.north93.zgame.api.global.utils.lang.SneakyThrow;

public class NorthChunkProvider extends ChunkProviderServer
{
    private static final MethodHandle SET_CHUNK_PROVIDER = MethodHandlesUtils.unreflectSetter(World.class, "chunkProvider");
    private static final MethodHandle GET_CHUNK_LOADER = MethodHandlesUtils.unreflectGetter(ChunkProviderServer.class, "chunkLoader");
 
    private static final Logger logger = LogManager.getLogger();
    
    private boolean generateNewChunksDisabled;
    private boolean keepingEntireWorldLoaded;
    
    private NorthChunkProvider(ChunkProviderServer original)
    {
        super(original.world, getChunkLoader(original), original.chunkGenerator);
    }
    
    static IChunkLoader getChunkLoader(ChunkProviderServer chunkProvider)
    {
        return (IChunkLoader) SneakyThrow.sneaky(() -> GET_CHUNK_LOADER.invoke(chunkProvider));
    }
    
    public static void inject(WorldServer world, boolean keepingEntireWorldLoaded, boolean generateNewChunksDisabled)
    {
        ChunkProviderServer original = world.getChunkProviderServer();
        
        NorthChunkProvider chunkProvider = new NorthChunkProvider(original);
        chunkProvider.setGenerateNewChunksDisabled(generateNewChunksDisabled);
        chunkProvider.setKeepingEntireWorldLoaded(keepingEntireWorldLoaded);
        
        CatchException.sneaky(() -> SET_CHUNK_PROVIDER.invoke(world, chunkProvider));
        logger.debug("Injected NorthChunkProvider to world {}", world.getWorld()::getName);
    }

    public boolean isGenerateNewChunksDisabled()
    {
        return generateNewChunksDisabled;
    }
    
    public void setGenerateNewChunksDisabled(boolean generateNewChunksDisabled)
    {
        this.generateNewChunksDisabled = generateNewChunksDisabled;
    }
    
    public boolean isKeepingEntireWorldLoaded()
    {
        return keepingEntireWorldLoaded;
    }
    
    public void setKeepingEntireWorldLoaded(boolean keepingEntireWorldLoaded)
    {
        this.keepingEntireWorldLoaded = keepingEntireWorldLoaded;
    }
    
    @Override
    public Chunk originalGetChunkAt(int x, int z)
    {
        if ( !generateNewChunksDisabled )
        {
            logger.debug("call super#originalGetChunkAt");
            return super.originalGetChunkAt(x, z);
        }
        
        Chunk chunk = super.originalGetOrLoadChunkAt(x, z);        
        if ( chunk != null )
        {
            return chunk;
        }
        
        
        logger.debug("Attempted to load dummy chunk at {}, {}", x, z);
        chunk = new DummyChunk(world, x, z);
        this.chunks.put(ChunkCoordIntPair.a(x, z), chunk);
        chunk.addEntities();
        chunk.loadNearby(this, this.chunkGenerator, true);
        
        return chunk;
    }

    @Override
    public boolean unloadChunks()
    {
        // Even chunk unloading is disabled we should unload our dummy chunks
        
        Iterator<Long> it = unloadQueue.iterator();
        while ( it.hasNext() )
        {
            long chunkCoord = it.next();
            if ( chunks.get(chunkCoord) instanceof DummyChunk )
            {
                Chunk chunk = chunks.remove(chunkCoord);
                logger.debug("Removed dummy chunk {}, {}", () -> chunk.locX, () -> chunk.locZ);
            }
        }
        
        return super.unloadChunks();
    }

    @Override
    public void saveChunk(Chunk chunk, boolean unloaded)
    {
        if ( chunk instanceof DummyChunk )
        {
            logger.debug("Cancelled saving dummy chunk {}, {}", () -> chunk.locX, () -> chunk.locZ);
            return;
        }
        
        super.saveChunk(chunk, unloaded);
    }
    
    @Override
    public void saveChunkNOP(Chunk chunk)
    {
        if ( chunk instanceof DummyChunk )
        {
            logger.debug("Cancelled saving NOP dummy chunk {}, {}", () -> chunk.locX, () -> chunk.locZ);
            return;
        }
        
        super.saveChunkNOP(chunk);
    }
    
    @Override
    public boolean unloadChunk(Chunk chunk, boolean save)
    {
        logger.trace("Loaded chunks {}", chunks.size());
        
        if ( !keepingEntireWorldLoaded )
        {
            return super.unloadChunk(chunk, save);
        }
        
        //logger.debug("Unloading chunk {} {}", chunk.locX, chunk.locZ);
        
        return false;
    }
}
