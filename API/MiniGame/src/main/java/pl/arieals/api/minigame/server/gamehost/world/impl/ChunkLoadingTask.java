package pl.arieals.api.minigame.server.gamehost.world.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import net.minecraft.server.v1_12_R1.ChunkProviderServer;
import net.minecraft.server.v1_12_R1.RegionFile;
import net.minecraft.server.v1_12_R1.RegionFileCache;
import net.minecraft.server.v1_12_R1.WorldServer;

import com.google.common.collect.Queues;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.reflections.DioriteReflectionUtils;
import org.diorite.commons.reflections.FieldAccessor;

import lombok.extern.slf4j.Slf4j;
import pl.arieals.api.minigame.server.gamehost.world.impl.blocker.WrappedChunkProviderServer;
import pl.north93.zgame.api.bukkit.server.impl.WorldInitializationHandler;
import pl.north93.zgame.api.bukkit.utils.xml.XmlChunk;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

@Slf4j
class ChunkLoadingTask implements Runnable
{
    private static final FieldAccessor chunkProvider = DioriteReflectionUtils.getField(net.minecraft.server.v1_12_R1.World.class, "chunkProvider");
    private static final int MIN_MEMORY = 15; // ponizej 15% przestajemy doczytywac chunki i czekamy na GC
    private final Queue<QueuedLoadingTask> tasks = Queues.synchronizedQueue(new ArrayDeque<>());
    private QueuedLoadingTask          activeTask;
    @Inject
    private WorldInitializationHandler initializationHandler;

    public void queueTask(final World world, final Set<XmlChunk> chunks, final LoadingProgressImpl progress)
    {
        this.tasks.add(new QueuedLoadingTask(world, Queues.newArrayDeque(chunks), progress, System.currentTimeMillis()));
        log.info("Queued loading {} chunks of {}", chunks.size(), world.getName());
    }

    @Override
    public void run()
    {
        final QueuedLoadingTask task = this.getCurrentTask();
        if (task == null)
        {
            //this.flushRegionCache(); // czyscimy region cache gdy skonczymy wykonywac wszystkie taski. //pomyslec nad dodaniem tego.
            return;
        }

        if (! this.checkRamUsage())
        {
            log.warn("Low free memory (under " + MIN_MEMORY + "%). Skipped chunk loading to prevent server crash.");
            System.gc(); // to tylko sugestia, ale probowac warto.
            return;
        }

        final Queue<XmlChunk> chunks = task.chunks;

        final long stopTime = System.currentTimeMillis() + 5;

        do
        {
            final XmlChunk chunk = chunks.poll();
            if (chunk == null)
            {
                this.completeTask(task);
                break;
            }
            task.world.loadChunk(chunk.getX(), chunk.getZ(), false);
        } while (System.currentTimeMillis() < stopTime);
    }

    private void completeTask(final QueuedLoadingTask task)
    {
        final long totalTime = System.currentTimeMillis() - task.startTime;
        log.info("Completed loading of world {} in {}ms", task.world.getName(), totalTime);

        if (task.isEmpty)
        {
            log.warn("World {} has empty list of chunks. Use WE wand and /mapaddchunks to add chunks. Blocking of new chunks disabled.", task.world.getName());
        }
        else
        {
            this.blockNewChunks(task.world);
        }

        this.activeTask = null;

        this.initializationHandler.callInitializers(task.world);
        task.progress.setCompleted();
    }

    // zwraca false jesli jest mniej niz MIN_MEMORY% wolnej pamieci
    private boolean checkRamUsage()
    {
        final double maxMemory = Runtime.getRuntime().maxMemory();
        final double freeMemory = Runtime.getRuntime().freeMemory();
        final double percent = 100;
        return (freeMemory / maxMemory) * percent > MIN_MEMORY;
    }

    private void flushRegionCache()//pomyslec nad dodaniem tego.
    {
        log.info("Flushing region file cache because all chunk loading task are completed.");
        synchronized (RegionFileCache.class)
        {
            final Map<File, RegionFile> cache = RegionFileCache.a;
            final Iterator<RegionFile> cacheIterator = cache.values().iterator();
            while (cacheIterator.hasNext())
            {
                try
                {
                    cacheIterator.next().c();
                }
                catch (final IOException e)
                {
                    log.error("failed to flush region cache", e);
                    continue;
                }
                cacheIterator.remove();
            }
        }
    }

    private void blockNewChunks(final World bukkitWorld)
    {
        final WorldServer world = ((CraftWorld) bukkitWorld).getHandle();
        final ChunkProviderServer oldProvider = world.getChunkProviderServer();

        final WrappedChunkProviderServer newProvider = new WrappedChunkProviderServer(oldProvider);
        chunkProvider.set(world, newProvider);
    }

    private QueuedLoadingTask getCurrentTask()
    {
        if (this.activeTask == null) // no active task
        {
            this.activeTask = this.tasks.poll();
        }
        return this.activeTask;
    }

    private static class QueuedLoadingTask
    {
        private final World world;
        private final Queue<XmlChunk> chunks;
        private final boolean isEmpty;
        private final LoadingProgressImpl progress;
        private final long startTime;

        private QueuedLoadingTask(final World world, final Queue<XmlChunk> chunks, final LoadingProgressImpl progress, final long startTime)
        {
            this.world = world;
            this.chunks = chunks;
            this.isEmpty = chunks.isEmpty();
            this.progress = progress;
            this.startTime = startTime;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("world", this.world).append("chunks", this.chunks).append("progress", this.progress).toString();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("tasks", this.tasks).append("activeTask", this.activeTask).toString();
    }
}
