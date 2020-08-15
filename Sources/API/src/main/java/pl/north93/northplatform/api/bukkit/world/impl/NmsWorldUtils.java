package pl.north93.northplatform.api.bukkit.world.impl;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import net.minecraft.server.v1_12_R1.ChunkProviderServer;
import net.minecraft.server.v1_12_R1.ChunkRegionLoader;
import net.minecraft.server.v1_12_R1.EntityTracker;
import net.minecraft.server.v1_12_R1.EnumDifficulty;
import net.minecraft.server.v1_12_R1.EnumGamemode;
import net.minecraft.server.v1_12_R1.IDataManager;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.RegionFile;
import net.minecraft.server.v1_12_R1.RegionFileCache;
import net.minecraft.server.v1_12_R1.ServerNBTManager;
import net.minecraft.server.v1_12_R1.WorldData;
import net.minecraft.server.v1_12_R1.WorldManager;
import net.minecraft.server.v1_12_R1.WorldServer;
import net.minecraft.server.v1_12_R1.WorldSettings;
import net.minecraft.server.v1_12_R1.WorldType;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.generator.ChunkGenerator;

import org.diorite.commons.io.DioriteFileUtils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.utils.lang.CatchException;
import pl.north93.northplatform.api.global.utils.lang.MethodHandlesUtils;

@Slf4j
@UtilityClass
class NmsWorldUtils
{
    private final MethodHandle WORLDS_GETTER = MethodHandlesUtils.unreflectGetter(CraftServer.class, "worlds");
    private final MethodHandle CHUNK_QUEUE_GETTER = MethodHandlesUtils.unreflectGetter(ChunkRegionLoader.class, "queue");

    WorldServer getMinecraftWorld(World bukkitWorld)
    {
        return ((CraftWorld) bukkitWorld).getHandle();
    }
    
    /**
     * Creates only WorldServer instance based world name
     * This method doesn't call any events and doesn't convert world.
     * 
     * @return created instance of world or null if it wasn't posibble
     */
    @SuppressWarnings("deprecation")
    WorldServer createWorldInstance(String name, WorldCreator creator)
    {
        Preconditions.checkState(Bukkit.isPrimaryThread());
        Preconditions.checkArgument(name != null, "World name cannot be null");
        Preconditions.checkState(Bukkit.getWorld(name) == null);
        
        if ( creator == null )
        {
            creator = new WorldCreator(name);
        }
        
        File folder = new File(Bukkit.getWorldContainer(), name);
        
        Preconditions.checkState(!folder.exists() || folder.isDirectory(), "File exists with name " + name + " and isn't directory");
        
        int dimension = 10 + MinecraftServer.getServer().worlds.size();
        boolean used = false;
        do
        {
            for ( WorldServer server : MinecraftServer.getServer().worlds )
            {
                used = server.dimension == dimension;
                if ( used )
                {
                    dimension++;
                    break;
                }
            }
        } while ( used );
        
        IDataManager sdm = new ServerNBTManager(Bukkit.getWorldContainer(), name, true, MinecraftServer.getServer().dataConverterManager);
        
        WorldData data = sdm.getWorldData();
        WorldSettings settings = null;
        if ( data == null )
        {
            settings = new WorldSettings(creator.seed(), EnumGamemode.valueOf(Bukkit.getDefaultGameMode().name()), false, false, 
                    WorldType.getType(creator.type().getName()));
            settings.setGeneratorSettings(creator.generatorSettings());
            
            data = new WorldData(settings, name);
        }
        
        data.checkName(name);
        
        CraftServer server = (CraftServer) Bukkit.getServer();
        ChunkGenerator generator = creator.generator();
        if ( generator == null )
        {
            generator = server.getGenerator(name);
        }
        
        WorldServer world = new WorldServer(MinecraftServer.getServer(), sdm, data, dimension, MinecraftServer.getServer().methodProfiler, 
                creator.environment(), creator.generator());
        world.b();
        
        String nameLc = name.toLowerCase(Locale.ENGLISH);
        
        if (Bukkit.getWorlds().stream().map(World::getName).noneMatch(nameLc::equalsIgnoreCase))
        {
            return null;
        }
        
        world.scoreboard = server.getScoreboardManager().getMainScoreboard().getHandle();
        world.tracker = new EntityTracker(world);
        world.addIWorldAccess(new WorldManager(MinecraftServer.getServer(), world));
        world.worldData.setDifficulty(EnumDifficulty.EASY);
        world.setSpawnFlags(true, true);
        world.keepSpawnInMemory = false;
        
        world.worldData.d(true);
        MinecraftServer.getServer().worlds.add(world);
        
        return world;
    }
    
    /**
     * Unloads world without saving.
     * Note that all pending saves will be cancelled.
     * If you want to save world you must use save method first.
     * @return
     */
    @SuppressWarnings({"deprecation"})
    boolean unloadWorld(World world, boolean force)
    {
        Preconditions.checkState(Bukkit.isPrimaryThread());
        Preconditions.checkArgument(world != null, "world cannot be null");
        
        WorldServer handle = ((CraftWorld) world).getHandle();
        
        Preconditions.checkState(MinecraftServer.getServer().worlds.contains(handle), "World isn't loaded");
        Preconditions.checkState(handle.dimension != 0, "Cannot unload main world");
        
        if ( handle.players.size() > 0 )
        {
            if ( force )
            {
                world.getPlayers().forEach(p -> p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation()));
            }
            else
            {
                return false;
            }
        }
        
        WorldUnloadEvent event = force ? new ForceWorldUnloadEvent(world) : new WorldUnloadEvent(world);
        
        Bukkit.getPluginManager().callEvent(event);
        if ( event.isCancelled() )
        {
            return false;
        }

        Map<String, World> worlds = getWorldsMap();
        
        worlds.remove(world.getName().toLowerCase(java.util.Locale.ENGLISH));
        MinecraftServer.getServer().worlds.remove(MinecraftServer.getServer().worlds.indexOf(handle));
        
        // XXX: shall we unload all chunks here in case of the world referece is keeped by someone else?
        
        // We have to cancel pending saves if any, because this may left lock on region files in filesystem even if we remove region cache
        cancelPendingSaves(handle);
        removeRegionCache(handle);
        
        return true;
    }

    @SneakyThrows
    private Map<String, World> getWorldsMap()
    {
        return (Map<String, World>) WORLDS_GETTER.invoke(Bukkit.getServer());
    }
    
    private void removeRegionCache(WorldServer world)
    {
        synchronized ( RegionFileCache.class )
        {
            File worldFolder = world.getDataManager().getDirectory();
            Iterator<Entry<File, RegionFile>> it = RegionFileCache.a.entrySet().iterator();
            
            while ( it.hasNext() )
            {
                Entry<File, RegionFile> entry = it.next();
                
                if ( DioriteFileUtils.contains(worldFolder, entry.getKey()) )
                {
                    it.remove();

                    // RegionFile#c() -> close()
                    CatchException.catchThrowable(() -> entry.getValue().c(), e ->
                    {
                        final String worldName = world.getWorld().getName();
                        log.error("Couldn't clenup region file cache for world {}", worldName, e);
                    });
                }
            }
        }
    }

    @SneakyThrows(Throwable.class)
    private void cancelPendingSaves(WorldServer world)
    {
        ChunkProviderServer chunkProvider = world.getChunkProviderServer();
        ChunkRegionLoader chunkLoader = (ChunkRegionLoader) NorthChunkProvider.getChunkLoader(chunkProvider);
        
        Queue<?> queue = (Queue<?>) CHUNK_QUEUE_GETTER.invoke(chunkLoader);
        
        // Syncronize to avoid race condition with ChunkRegionLoader#processSaveQueueEntry
        synchronized ( chunkLoader )
        {
            log.debug("Cancelled saving {} chunks", queue.size());
            queue.clear();
        }
    }
    
    /**
     * Force saving world, the save process should be syncronized to main server thread, 
     * so world will be saved when that method exited
     */
    void forceSave(World bukkitWorld)
    {
        log.debug("Saving chunks for world {}", bukkitWorld.getName());
        
        WorldServer handle = getMinecraftWorld(bukkitWorld);
        
        try
        {
            handle.save(true, null);
            handle.saveLevel(); // name of this method should be "waitForLevelSaving"
            log.debug("Saved chunks for world {}", bukkitWorld.getName());
        }
        catch ( Throwable e )
        {
            log.error("Couldn't save world {}", bukkitWorld.getName(), e);
        }
    }
}

class ForceWorldUnloadEvent extends WorldUnloadEvent
{

    public ForceWorldUnloadEvent(World world)
    {
        super(world);
    }
    
    @Override
    public boolean isCancelled()
    {
        return false;
    }
}
