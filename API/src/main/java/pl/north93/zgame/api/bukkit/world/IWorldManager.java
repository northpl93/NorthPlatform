package pl.north93.zgame.api.bukkit.world;

import java.util.Collection;

import org.bukkit.World;
import org.bukkit.WorldCreator;

import pl.north93.zgame.api.bukkit.utils.xml.XmlChunk;

public interface IWorldManager
{
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
     */
    boolean tryUnloadWorld(World world);
    
    /**
     * Forces to unload world, even if there are players or plugin cancelled event.
     */
    void unloadWorld(World world);
    
    /**
     * Trim world to contains only given chunk and save it to new directory.
     */
    void trimWorld(World world, String target, Collection<? extends XmlChunk> chunks);
}
