package pl.arieals.api.minigame.server.gamehost.utils;

import org.bukkit.scheduler.BukkitRunnable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public abstract class Countdown extends BukkitRunnable
{
    private int time;

    public Countdown(final int time)
    {
        this.time = time;
    }

    @Override
    public final void run()
    {
        if (this.time == 0)
        {
            this.end();
            this.cancel();
        }
        else
        {
            this.loop(this.time);
            this.time--;
        }
    }

    public final int getTime()
    {
        return this.time;
    }

    protected abstract void loop(int time);

    protected abstract void end();

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("time", this.time).toString();
    }
}
