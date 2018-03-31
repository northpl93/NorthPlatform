package pl.arieals.api.minigame.server.gamehost.world.impl;

import org.bukkit.World;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.world.ILoadingProgress;

class LoadingProgressImpl implements ILoadingProgress
{
    private final World world;
    private boolean     isCompleted;
    private Runnable    completeTask;

    public LoadingProgressImpl(final World world)
    {
        this.world = world;
    }

    void setCompleted()
    {
        synchronized (this)
        {
            this.isCompleted = true;
            if (this.completeTask != null)
            {
                this.completeTask.run();
            }
        }
    }

    @Override
    public World getWorld()
    {
        return this.world;
    }

    @Override
    public boolean isComplete()
    {
        synchronized (this)
        {
            return this.isCompleted;
        }
    }

    @Override
    public void onComplete(final Runnable runnable)
    {
        synchronized (this)
        {
            if (this.isCompleted)
            {
                runnable.run();
            }
            else
            {
                this.completeTask = runnable;
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("world", this.world).append("isCompleted", this.isCompleted).toString();
    }
}
