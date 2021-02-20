package pl.north93.northplatform.api.minigame.shared.api.statistics;

import org.bson.conversions.Bson;

/**
 * Filtr używane są do zawężenia kryteriów poszukiwania wyników.
 */
public interface IStatisticFilter
{
    Bson getCondition(IStatistic<?, ?> statistic);

    Bson getSort(IStatistic<?, ?> statistic);
}
