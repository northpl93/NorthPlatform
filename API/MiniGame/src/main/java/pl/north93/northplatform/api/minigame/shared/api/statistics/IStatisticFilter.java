package pl.north93.northplatform.api.minigame.shared.api.statistics;

import org.bson.Document;

/**
 * Filtr używane są do zawężenia kryteriów poszukiwania wyników.
 */
public interface IStatisticFilter
{
    void appendConditions(IStatistic<?> statistic, Document query);

    void appendSort(IStatistic<?> statistic, Document sort);
}
