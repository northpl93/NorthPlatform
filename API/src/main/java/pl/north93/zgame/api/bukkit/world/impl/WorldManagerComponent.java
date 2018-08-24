package pl.north93.zgame.api.bukkit.world.impl;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.WorldServer;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import org.apache.commons.io.FileUtils;

import lombok.extern.slf4j.Slf4j;
import pl.north93.zgame.api.bukkit.world.ChunkLocation;
import pl.north93.zgame.api.bukkit.world.IWorldLoadCallback;
import pl.north93.zgame.api.bukkit.world.IWorldManager;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

@Slf4j
/*default*/ class WorldManagerComponent extends Component implements IWorldManager
{
    @Inject
    private ChunkLoadManager chunkLoadManager;
    
    @Override
    protected void enableComponent()
    {
        // TODO: handle main world
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public boolean copyWorld(final String name, final File template)
    {
        final File newWorldDirectory = new File(Bukkit.getWorldContainer(), name);
        try
        {
            FileUtils.copyDirectory(template, newWorldDirectory);
            return true;
        }
        catch (final IOException e)
        {
            log.error("Failed to copy world {} from {}", name, template, e);
            return false;
        }
    }

    @Override
    public IWorldLoadCallback loadWorld(String name, WorldCreator creator, boolean keepEntireWorldLoaded, boolean disableGenerateNewChunks)
    {
        WorldServer worldServer = NmsWorldUtils.createWorldInstance(name, null);
        
        if ( worldServer == null )
        {
            return null;
        }
        
        Bukkit.getPluginManager().callEvent(new WorldInitEvent(worldServer.getWorld()));
        NorthChunkProvider.inject(worldServer, keepEntireWorldLoaded, disableGenerateNewChunks);

        log.info("Initialized world {} (keepWorldLoaded: {}, disableGenerateChunks: {})", name, keepEntireWorldLoaded, disableGenerateNewChunks);

        Bukkit.getPluginManager().callEvent(new WorldLoadEvent(worldServer.getWorld()));
        
        WorldLoadCallback callback = new WorldLoadCallback(worldServer.getWorld());
        
        if ( keepEntireWorldLoaded )
        {
            chunkLoadManager.start(ChunkLoadTask.loadAllChunks(callback));
        }
        else if ( worldServer.getWorld().getKeepSpawnInMemory() )
        {
            chunkLoadManager.start(ChunkLoadTask.loadSpawn(callback));
        }
        else
        {
            callback.callComplete();
        }
        
        return callback;
    }

    @Override
    public boolean tryUnloadWorld(World world)
    {
        return unloadWorld0(world, false);
    }

    @Override
    public void unloadWorld(World world)
    {
        Preconditions.checkState(unloadWorld0(world, true));
    }

    @Override
    public void unloadAndDeleteWorld(final World world)
    {
        this.unloadWorld0(world, true);
        try
        {
            FileUtils.deleteDirectory(world.getWorldFolder());
        }
        catch (final IOException e)
        {
            log.error("Failed to delete world {} directory", world.getName(), e);
        }
    }

    @Override
    public void unloadAndDeleteWorld(final String worldName)
    {
        final World world = Bukkit.getWorld(worldName);
        if (world != null)
        {
            this.unloadAndDeleteWorld(world);
        }
    }

    private boolean unloadWorld0(World world, boolean force)
    {
        boolean result = NmsWorldUtils.unloadWorld(world, force);
        
        if ( result )
        {
            log.info("Unloaded world " + world.getName());
        }
        
        return result;
    }
    
    @Override
    public void save(World world)
    {
        NmsWorldUtils.forceSave(world);   
    }
    
    @Override
    public void trimWorld(World source, String targetName, Collection<? extends ChunkLocation> chunks)
    {
        final WorldCreator creator = new WorldCreator(targetName);
        creator.generateStructures(false);
        creator.generatorSettings("0");
        creator.type(WorldType.FLAT);
        creator.environment(World.Environment.NORMAL);
        creator.generator(EmptyChunkGenerator.getInstance()); // We use empty generator to faster generate chunks that we're going to replace with source's ones anyway
        
        final World target = NmsWorldUtils.createWorldInstance(targetName, creator).getWorld();
        Preconditions.checkState(target != null);
        
        Location spawn = source.getSpawnLocation();
        target.setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
        
        for (final ChunkLocation xmlChunk : chunks)
        {
            final Chunk sourceChunk = ((CraftChunk) source.getChunkAt(xmlChunk.getX(), xmlChunk.getZ())).getHandle();
            final Chunk targetChunk = ((CraftChunk) target.getChunkAt(xmlChunk.getX(), xmlChunk.getZ())).getHandle();

            System.arraycopy(sourceChunk.getSections(), 0, targetChunk.getSections(), 0, sourceChunk.getSections().length);
            System.arraycopy(sourceChunk.heightMap, 0, targetChunk.heightMap, 0, sourceChunk.heightMap.length);
            System.arraycopy(sourceChunk.entitySlices, 0, targetChunk.entitySlices, 0, sourceChunk.entitySlices.length);

            targetChunk.tileEntities.clear();
            targetChunk.tileEntities.putAll(sourceChunk.getTileEntities());
        }

        NmsWorldUtils.forceSave(target);
        NmsWorldUtils.unloadWorld(target, true);
    }
}
