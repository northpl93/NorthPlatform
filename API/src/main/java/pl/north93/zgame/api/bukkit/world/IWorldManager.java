package pl.north93.zgame.api.bukkit.world;

import java.io.File;
import java.util.Collection;

import org.bukkit.World;
import org.bukkit.WorldCreator;

public interface IWorldManager
{
    boolean copyWorld(String name, File template);

    /**
     * Try to load world with a given name, return null when loading world haven't been posible.
     * Note that this will distribute chunk loading into several ticks to balance server load.
     */
    default IWorldLoadCallback loadWorld(String name)
    {
        return loadWorld(name, null, false, false);
    }
    
    /**
     * Try to load world with a given name, return null when loading world haven't been posible.
     * Note that this will distribute chunk loading into several ticks to balance server load.
     */
    default IWorldLoadCallback loadWorld(String name, WorldCreator creator)
    {
        return loadWorld(name, creator, false, false);
    }
    
    /**
     * Try to load world with a given name, return null when loading world haven't been posible.
     * Note that this will distribute chunk loading into several ticks to balance server load.
     */
    default IWorldLoadCallback loadWorld(String name, boolean keepEntireWorldLoaded, boolean disableGenerateNewChunks)
    {
        return loadWorld(name, null, keepEntireWorldLoaded, disableGenerateNewChunks);
    }
    
    /**
     * Try to load world with a given name, return null when loading world haven't been posible.
     * Note that this will distribute chunk loading into several ticks to balance server load.
     */
    IWorldLoadCallback loadWorld(String name, WorldCreator creator, boolean keepEntireWorldLoaded, boolean disableGenerateNewChunks);
    
    /**
     * Try to unload world, if this is not possible return false.
     * Note that this method won't save chunks. 
     */
    boolean tryUnloadWorld(World world);
    
    /**
     * Forces to unload world, even if there are players or plugin cancelled event.
     * Note that this method won't save chunks.
     */
    void unloadWorld(World world);

    /**
     * Forces to unload world, even if there are players or plugin cancelled event.
     * Then deletes world directory.
     */
    void unloadAndDeleteWorld(World world);

    void unloadAndDeleteWorld(String worldName);
    
    /**
     * Forces given world to save chunks
     * Note that this method will save chunks synchronously in server thread
     */
    void save(World world);
    
    // TODO: async save
    
    /**
     * Trim world to contains only given chunk and save it to new directory.
     */
    void trimWorld(World world, String target, Collection<? extends ChunkLocation> chunks);
}
