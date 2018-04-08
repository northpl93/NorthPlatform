package pl.arieals.api.minigame.server.gamehost.scheduler;

import org.bukkit.craftbukkit.v1_12_R1.scheduler.CraftTask;
import org.bukkit.scheduler.BukkitTask;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.reflections.DioriteReflectionUtils;
import org.diorite.commons.reflections.FieldAccessor;

class WrappedBukkitTask implements BukkitTaskWrapper
{
    private static final FieldAccessor<Long> craftTaskPeriod  = DioriteReflectionUtils.getField(CraftTask.class, "period", long.class);
    private static final FieldAccessor<Long> craftTaskNextRun = DioriteReflectionUtils.getField(CraftTask.class, "nextRun", long.class);
    private final BukkitTask bukkitTask;
    private final long       scheduledAt;

    public WrappedBukkitTask(final BukkitTask bukkitTask)
    {
        this.bukkitTask = bukkitTask;
        this.scheduledAt = System.currentTimeMillis();
    }

    @Override
    public void run()
    {
        if (this.bukkitTask instanceof CraftTask)
        {
            ((CraftTask) this.bukkitTask).run();
            return;
        }
        throw new AssertionError("Only CraftTask is supported");
    }

    @Override
    public long getScheduledAt()
    {
        return this.scheduledAt;
    }

    @Override
    public boolean isRepeated()
    {
        return this.getPeriod() > 0;
    }

    @Override
    public boolean isCancelled()
    {
        return this.getPeriod() == -2;
    }

    @Override
    public long getPeriod()
    {
        return craftTaskPeriod.get(this.bukkitTask);
    }

    @Override
    public long getNextRun()
    {
        return craftTaskNextRun.get(this.bukkitTask);
    }

    @Override
    public void setNextRun(final long nextRun)
    {
        craftTaskNextRun.set(this.bukkitTask, nextRun);
    }

    @Override
    public void cancel()
    {
        if (this.isCancelled())
        {
            return;
        }
        this.bukkitTask.cancel();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("bukkitTask", this.bukkitTask).append("scheduledAt", this.scheduledAt).toString();
    }
}
