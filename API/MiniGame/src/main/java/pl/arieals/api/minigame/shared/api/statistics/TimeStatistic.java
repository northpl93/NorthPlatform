package pl.arieals.api.minigame.shared.api.statistics;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TimeStatistic implements IStatisticEncoder
{
    private long milis;

    public TimeStatistic(final long milis)
    {
        this.milis = milis;
    }

    @Override
    public long get()
    {
        return this.milis;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("milis", this.milis).toString();
    }
}
