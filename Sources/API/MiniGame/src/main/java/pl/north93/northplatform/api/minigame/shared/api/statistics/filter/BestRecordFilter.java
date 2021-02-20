package pl.north93.northplatform.api.minigame.shared.api.statistics.filter;

import org.bson.conversions.Bson;

import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticFilter;

public class BestRecordFilter implements IStatisticFilter
{
    @Override
    public Bson getCondition(final IStatistic<?, ?> statistic)
    {
        return null;
    }

    @Override
    public Bson getSort(final IStatistic<?, ?> statistic)
    {
        return statistic.getDbComposer().bestRecordQuery();
    }
}
