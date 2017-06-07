package pl.arieals.api.minigame.server.gamehost.world.impl;

import static java.text.MessageFormat.format;


import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

import com.google.common.collect.Queues;

import org.bukkit.World;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;

class ChunkLoadingTask implements Runnable
{
    private final Queue<QueuedLoadingTask> tasks = Queues.synchronizedQueue(new ArrayDeque<>());
    private QueuedLoadingTask activeTask;
    @Inject
    private Logger            logger;

    public void queueTask(final World world, final List<Pair<Integer, Integer>> chunks, final LoadingProgressImpl progress)
    {
        this.tasks.add(new QueuedLoadingTask(world, new ArrayDeque<>(chunks), progress, System.currentTimeMillis()));
        this.logger.info(format("Queued loading {0} chunks of {1}", chunks.size(), world.getName()));
    }

    @Override
    public void run()
    {
        final QueuedLoadingTask task = this.getCurrentTask();
        if (task == null)
        {
            return;
        }

        final Queue<Pair<Integer, Integer>> chunks = task.chunks;

        final long stopTime = System.currentTimeMillis() + 5;

        do
        {
            final Pair<Integer, Integer> chunk = chunks.poll();
            if (chunk == null)
            {
                final long totalTime = System.currentTimeMillis() - task.startTime;
                this.logger.info(format("Completed loading of world {0} in {1}ms", task.world.getName(), totalTime));

                this.activeTask = null;
                task.progress.setCompleted();
                break;
            }
            task.world.loadChunk(chunk.getKey(), chunk.getValue(), false);
        } while (System.currentTimeMillis() < stopTime);
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
        private final Queue<Pair<Integer, Integer>> chunks;
        private final LoadingProgressImpl progress;
        private final long startTime;

        private QueuedLoadingTask(final World world, final Queue<Pair<Integer, Integer>> chunks, final LoadingProgressImpl progress, final long startTime)
        {
            this.world = world;
            this.chunks = chunks;
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
