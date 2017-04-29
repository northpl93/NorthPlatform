package pl.arieals.api.minigame.server.gamehost.world.impl;

import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import org.bukkit.World;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;

class ChunkLoadingTask implements Runnable
{
    private final Queue<QueuedLoadingTask> tasks = new ArrayDeque<>();
    private QueuedLoadingTask activeTask;

    public void queueTask(final World world, final List<Pair<Integer, Integer>> chunks, final LoadingProgressImpl progress)
    {
        synchronized (this.tasks)
        {
            this.tasks.add(new QueuedLoadingTask(world, new ArrayDeque<>(chunks), progress));
        }
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

        final long stopTime = System.currentTimeMillis() + 10;

        do
        {
            final Pair<Integer, Integer> chunk = chunks.poll();
            if (chunk == null)
            {
                System.out.println(MessageFormat.format("Completed loading of world {0}", task.world.getName()));

                this.activeTask = null;
                task.progress.setCompleted();
                break;
            }
            task.world.loadChunk(chunk.getKey(), chunk.getValue());
        } while (System.currentTimeMillis() < stopTime);
    }

    private QueuedLoadingTask getCurrentTask()
    {
        if (this.activeTask == null || this.activeTask.chunks.isEmpty()) // no active tasks or completed task
        {
            synchronized (this.tasks)
            {
                return this.activeTask = this.tasks.poll();
            }
        }
        return this.activeTask;
    }

    private static class QueuedLoadingTask
    {
        private final World world;
        private final Queue<Pair<Integer, Integer>> chunks;
        private final LoadingProgressImpl progress;

        private QueuedLoadingTask(final World world, final Queue<Pair<Integer, Integer>> chunks, final LoadingProgressImpl progress)
        {
            this.world = world;
            this.chunks = chunks;
            this.progress = progress;
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
