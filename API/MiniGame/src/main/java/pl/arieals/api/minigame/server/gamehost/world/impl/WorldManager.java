package pl.arieals.api.minigame.server.gamehost.world.impl;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldInitEvent;

import org.apache.commons.io.FileUtils;

import pl.arieals.api.minigame.server.gamehost.world.ILoadingProgress;
import pl.arieals.api.minigame.server.gamehost.world.IWorldManager;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.Main;
import pl.north93.zgame.api.bukkit.utils.region.IRegion;
import pl.north93.zgame.api.global.component.annotations.PostInject;

public class WorldManager implements IWorldManager
{
    private BukkitApiCore apiCore;
    private Logger        logger;
    private ChunkLoadingTask chunkLoadingTask;
    private final List<World> worlds = new ArrayList<>();

    @PostInject
    public void postInject()
    {
        this.chunkLoadingTask = new ChunkLoadingTask();
        final Main plugin = this.apiCore.getPluginMain();
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this.chunkLoadingTask, 5, 5);
    }

    @Override
    public LoadingProgressImpl loadWorld(final String name, final File source, final IRegion gameRegion)
    {
        final WorldCreator creator = new WorldCreator(name);
        creator.generateStructures(false);
        creator.generatorSettings("0");
        creator.type(WorldType.FLAT);
        creator.environment(World.Environment.NORMAL);

        final File mapDir = new File(Bukkit.getWorldContainer(), name);
        try
        {
            FileUtils.copyDirectory(source, mapDir);
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Failed to copy files for map " + name, e);
        }

        final World world = creator.createWorld();
        this.worlds.add(world);

        final LoadingProgressImpl progress = new LoadingProgressImpl(world);
        this.chunkLoadingTask.queueTask(world, gameRegion.getChunksCoordinates(), progress);

        this.logger.info(MessageFormat.format("Queued loading chunks of {0}", name));

        return progress;
    }

    @Override
    public ILoadingProgress regenWorld(final String name, final File source, final IRegion gameRegion)
    {
        final World world = Bukkit.getWorld(name);
        if (world != null && ! this.unloadWorld(name))
        {
            throw new RuntimeException("Can't regenerate world " + name + ". Failed to unload previous world.");
        }
        return this.loadWorld(name, source, gameRegion);
    }

    @Override
    public boolean unloadWorld(final String name)
    {
        final World world = Bukkit.getWorld(name);
        this.worlds.remove(world);
        return Bukkit.unloadWorld(world, false);
    }

    @EventHandler
    public void onWorldInit(final WorldInitEvent event)
    {
        event.getWorld().setKeepSpawnInMemory(false); // do not load spawn area while loading
    }

    @EventHandler
    public void onChunkGenerate(final ChunkLoadEvent event)
    {
        if (event.isNewChunk() && this.worlds.contains(event.getWorld()))
        {
            event.getChunk().unload(false); // do not generate new chunks in managed worlds
        }
    }

    @EventHandler
    public void onChunkUnload(final ChunkUnloadEvent event)
    {
        event.setSaveChunk(false); // do not save anything on gamehost // TODO TEMPORARY
        if (this.worlds.contains(event.getWorld()))
        {
            event.setCancelled(true); // do not unload chunks from managed worlds
        }
    }
}
