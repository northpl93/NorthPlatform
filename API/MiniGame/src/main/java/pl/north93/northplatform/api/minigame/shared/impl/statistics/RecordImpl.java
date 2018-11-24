package pl.north93.northplatform.api.minigame.shared.impl.statistics;

import java.time.Instant;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.shared.api.statistics.IRecord;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticUnit;

class RecordImpl<UNIT extends IStatisticUnit> implements IRecord<UNIT>
{
    private final IStatistic<UNIT> statistic;
    private final IStatisticHolder holder;
    private final Instant          recordTime;
    private final UNIT             value;

    public RecordImpl(final IStatistic<UNIT> statistic, final IStatisticHolder holder, final Instant recordTime, final UNIT value)
    {
        this.statistic = statistic;
        this.holder = holder;
        this.recordTime = recordTime;
        this.value = value;
    }

    @Override
    public IStatistic<UNIT> getStatistic()
    {
        return this.statistic;
    }

    @Override
    public IStatisticHolder getHolder()
    {
        return this.holder;
    }

    @Override
    public Instant getRecordedAt()
    {
        return this.recordTime;
    }

    @Override
    public UNIT getValue()
    {
        return this.value;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("statistic", this.statistic).append("holder", this.holder).append("recordTime", this.recordTime).append("value", this.value).toString();
    }
}
