package pl.north93.northplatform.api.bukkit.world.impl;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import net.minecraft.server.v1_12_R1.MinecraftServer;

import org.bukkit.Location;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.world.ChunkLocation;

@Slf4j
/*default*/ class ChunkLoadTask
{
    private final WorldLoadCallback    callback;
    private final Queue<ChunkLocation> chunkQueue;
    private final int                  chunksSize;
    
    private boolean completed;
    
    private int startTick;
    
    ChunkLoadTask(WorldLoadCallback callback, Collection<ChunkLocation> chunksToLoad)
    {
        this.callback = callback;
        this.chunkQueue = new ArrayDeque<>(chunksToLoad);
        this.chunksSize = chunksToLoad.size();
    }
    
    public WorldLoadCallback getCallback()
    {
        return callback;
    }
    
    public boolean isCompleted()
    {
        return completed;
    }
    
    public void onStarted()
    {
        log.debug("Total chunks to load for world {} is {}", callback.getWorld().getName(), chunksSize);
        
        startTick = MinecraftServer.currentTick;
    }
    
    public void onFinished()
    {
        int ticks = MinecraftServer.currentTick - startTick;
        log.info("Loaded {} chunks for world {} in {} ticks", chunksSize, callback.getWorld().getName(), ticks);
        callback.callComplete();
    }
    
    public void onCancelled()
    {
        log.debug("Cancelled loading chunks for world {}", callback.getWorld().getName());
        // XXX: shall we call on complete? I suppose not
    }
    
    public void tick()
    {
        long stop = System.nanoTime() + 5_000_000;
        do
        {
            if ( chunkQueue.peek() == null )
            {
                completed = true;
                return;
            }

            ChunkLocation chunk = chunkQueue.poll();
            
            callback.getWorld().loadChunk(chunk.getX(), chunk.getZ(), false);
        } while ( System.nanoTime() < stop );
    }
    
    public static ChunkLoadTask loadAllChunks(WorldLoadCallback callback)
    {
        long start = System.nanoTime();
        
        Set<ChunkLocation> chunks = RegionFileUtils.getGeneratedChunks(callback.getWorld());
        
        long time = System.nanoTime() - start;
        log.debug("Preparing list of chunks for world {} took {} ms", callback.getWorld().getName(), time / 1_000_000);
        
        return new ChunkLoadTask(callback, chunks);
    }
    
    public static ChunkLoadTask loadSpawn(WorldLoadCallback callback)
    {
        Set<ChunkLocation> result = new HashSet<>();
        
        Location spawn = callback.getWorld().getSpawnLocation();
        int range = NmsWorldUtils.getMinecraftWorld(callback.getWorld()).paperConfig.keepLoadedRange;
        
        for ( int i = -range; i < range; i += 16 )
        {
            for ( int j = -range; j < range; j += 16 )
            {
                result.add(new ChunkLocation((spawn.getBlockX() + i ) << 4, (spawn.getBlockZ() + j ) << 4));
            }
        }
        
        return new ChunkLoadTask(callback, result);
    }
}



