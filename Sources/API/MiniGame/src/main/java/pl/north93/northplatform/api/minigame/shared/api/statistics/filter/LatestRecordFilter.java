package pl.north93.northplatform.api.minigame.shared.api.statistics.filter;

import org.bson.Document;

import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticFilter;

public class LatestRecordFilter implements IStatisticFilter
{
    @Override
    public void appendConditions(final IStatistic<?, ?> statistic, final Document query)
    {

    }

    @Override
    public void appendSort(final IStatistic<?, ?> statistic, final Document sort)
    {
        sort.append("time", 1);
    }
}
