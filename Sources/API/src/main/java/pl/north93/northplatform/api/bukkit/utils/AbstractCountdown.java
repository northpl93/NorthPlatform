package pl.north93.northplatform.api.bukkit.utils;

import org.bukkit.scheduler.BukkitRunnable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCountdown extends BukkitRunnable
{
    private int time;

    public AbstractCountdown(final int time)
    {
        this.time = time;
    }

    @Override
    public final void run()
    {
        if (this.time == 0)
        {
            try
            {
                this.end();
            }
            catch (final Exception e)
            {
                log.error("An exception has been throw while ending countdown.", e);
            }
            this.cancel();
        }
        else
        {
            try
            {
                this.loop(this.time);
            }
            catch (final Exception e)
            {
                log.error("An exception has been throw in countdown loop.", e);
            }
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
