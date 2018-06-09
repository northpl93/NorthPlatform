package pl.north93.zgame.api.bukkit.world.impl;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Location;

import net.minecraft.server.v1_12_R1.MinecraftServer;

import pl.north93.zgame.api.bukkit.utils.xml.XmlChunk;

public class ChunkLoadTask
{
    private static final Logger logger = LogManager.getLogger();
    
    private final WorldLoadCallback callback;
    private final Queue<XmlChunk> chunkQueue;
    private final int chunksSize;
    
    private boolean completed;
    
    private int startTick;
    
    ChunkLoadTask(WorldLoadCallback callback, Collection<XmlChunk> chunksToLoad)
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
        logger.debug("Total chunks to load for world {} is {}", () -> callback.getWorld().getName(), () -> chunksSize);
        
        startTick = MinecraftServer.currentTick;
    }
    
    public void onFinished()
    {
        int ticks = MinecraftServer.currentTick - startTick;
        logger.info("Loaded {} chunks for world {} in {} ticks", () -> chunksSize, () -> callback.getWorld().getName(), () -> ticks);
        callback.callComplete();
    }
    
    public void onCancelled()
    {
        logger.debug("Cancelled loading chunks for world {}", () -> callback.getWorld().getName());
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
            
            XmlChunk chunk = chunkQueue.poll();
            
            callback.getWorld().loadChunk(chunk.getX(), chunk.getZ(), false);
        } while ( System.nanoTime() < stop );
    }
    
    public static ChunkLoadTask loadAllChunks(WorldLoadCallback callback)
    {
        long start = System.nanoTime();
        
        Set<XmlChunk> chunks = RegionFileUtils.getGeneratedChunks(callback.getWorld());
        
        long time = System.nanoTime() - start;
        logger.debug("Preparing list of chunks for world {} took {} ms", () -> callback.getWorld().getName(), () -> time / 1_000_000);
        
        return new ChunkLoadTask(callback, chunks);
    }
    
    public static ChunkLoadTask loadSpawn(WorldLoadCallback callback)
    {
        Set<XmlChunk> result = new HashSet<>();
        
        Location spawn = callback.getWorld().getSpawnLocation();
        int range = NmsWorldUtils.getMinecraftWorld(callback.getWorld()).paperConfig.keepLoadedRange;
        
        for ( int i = -range; i < range; i += 16 )
        {
            for ( int j = -range; j < range; j += 16 )
            {
                result.add(new XmlChunk((spawn.getBlockX() + i ) << 4, (spawn.getBlockZ() + j ) << 4));
            }
        }
        
        return new ChunkLoadTask(callback, result);
    }
}



