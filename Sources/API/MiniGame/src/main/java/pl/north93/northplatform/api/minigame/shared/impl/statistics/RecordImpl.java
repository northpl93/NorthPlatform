package pl.north93.northplatform.api.minigame.shared.impl.statistics;

import lombok.AllArgsConstructor;
import lombok.ToString;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IRecord;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticUnit;

import java.time.Instant;

@ToString
@AllArgsConstructor
class RecordImpl<T, UNIT extends IStatisticUnit<T>> implements IRecord<T, UNIT>
{
    private final IStatistic<T, UNIT> statistic;
    private final IStatisticHolder holder;
    private final Instant recordTime;
    private final UNIT value;

    @Override
    public IStatistic<T, UNIT> getStatistic()
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
}
