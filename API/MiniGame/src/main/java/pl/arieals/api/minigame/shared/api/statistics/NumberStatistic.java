package pl.arieals.api.minigame.shared.api.statistics;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class NumberStatistic implements IStatisticEncoder
{
    private final long value;

    public NumberStatistic(final long value)
    {
        this.value = value;
    }

    @Override
    public long get()
    {
        return this.value;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("value", this.value).toString();
    }
}
