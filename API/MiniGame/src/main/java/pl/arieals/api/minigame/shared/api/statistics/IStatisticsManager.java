package pl.arieals.api.minigame.shared.api.statistics;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IStatisticsManager
{
    IStatisticHolder getHolder(UUID uuid);

    <UNIT extends IStatisticUnit> CompletableFuture<IRecord<UNIT>> getBestRecord(IStatistic<UNIT> statistic);

    <UNIT extends IStatisticUnit> CompletableFuture<UNIT> getAverage(IStatistic<UNIT> statistic);
}
