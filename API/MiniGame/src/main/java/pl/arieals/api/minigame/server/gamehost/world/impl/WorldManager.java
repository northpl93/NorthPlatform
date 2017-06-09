package pl.arieals.api.minigame.server.gamehost.world.impl;

import static java.text.MessageFormat.format;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.craftbukkit.v1_10_R1.CraftChunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldInitEvent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.world.ILoadingProgress;
import pl.arieals.api.minigame.server.gamehost.world.IWorldManager;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.Main;
import pl.north93.zgame.api.bukkit.utils.xml.XmlChunk;
import pl.north93.zgame.api.global.component.annotations.PostInject;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class WorldManager implements IWorldManager, Listener
{
    @Inject
    private BukkitApiCore    apiCore;
    private NmsWorldHelper   worldHelper;
    private ChunkLoadingTask chunkLoadingTask;
    private final List<World> worlds = new ArrayList<>();
    private final Set<Chunk> chunksToUnload = Collections.newSetFromMap(new WeakHashMap<>());

    @PostInject
    public void postInject()
    {
        this.worldHelper = new NmsWorldHelper();
        this.chunkLoadingTask = new ChunkLoadingTask();
        final Main plugin = this.apiCore.getPluginMain();
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this.chunkLoadingTask, 0, 2);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public LoadingProgressImpl loadWorld(final String name, final File source, final Set<XmlChunk> chunks)
    {
        final WorldCreator creator = new WorldCreator(name);
        creator.generateStructures(false);
        creator.generatorSettings("0");
        creator.type(WorldType.FLAT);
        creator.environment(World.Environment.NORMAL);

        final World world = this.worldHelper.createWorld(creator);
        this.worlds.add(world);

        // ustawia savingDisabled na true
        // przy profilowaniu wyszlo ze proba unloadowania chunkow zjada sporo czasu ticka
        // ChunkProviderServer.unloadChunks
        world.setAutoSave(false);

        final LoadingProgressImpl progress = new LoadingProgressImpl(world);

        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            // kopiujemy pliki asynchronicznie zeby nie mulic glownego
            // watku serwera
            final File mapDir = new File(Bukkit.getWorldContainer(), name);
            final File regionDir = new File(mapDir, "region");
            try
            {
                this.apiCore.debug(format("Copying files of world {0}.", world.getName()));
                if (regionDir.exists() && regionDir.isDirectory())
                {
                    FileUtils.cleanDirectory(regionDir);
                }
                FileUtils.copyDirectory(source, mapDir);
            }
            catch (final IOException e)
            {
                throw new RuntimeException("Failed to copy files for map " + name, e);
            }
            this.chunkLoadingTask.queueTask(world, chunks, progress);
        });

        return progress;
    }

    @Override
    public ILoadingProgress regenWorld(final String name, final File source, final Set<XmlChunk> chunks)
    {
        final World world = Bukkit.getWorld(name);
        if (world != null && ! this.unloadWorld(name))
        {
            throw new RuntimeException("Can't regenerate world " + name + ". Failed to unload previous world.");
        }
        return this.loadWorld(name, source, chunks);
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
        ((CraftChunk) event.getChunk()).getHandle().mustSave = false; // this can override flag in Chunk#unload(boolean)
    }

    @EventHandler
    public void onChunkUnload(final ChunkUnloadEvent event)
    {
        event.setSaveChunk(false); // do not save anything on gamehost
        if (this.worlds.contains(event.getWorld()))
        {
            event.setCancelled(true); // do not unload chunks from managed worlds
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("worlds", this.worlds).toString();
    }
}
