package pl.arieals.api.minigame.shared.api.statistics;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IStatisticsManager
{
    IStatisticHolder getHolder(HolderIdentity holder);

    default IStatisticHolder getPlayerHolder(final UUID playerId)
    {
        return this.getHolder(new HolderIdentity("player", playerId));
    }

    <UNIT extends IStatisticUnit> CompletableFuture<IRanking> getRanking(IStatistic<UNIT> statistic, int size);

    <UNIT extends IStatisticUnit> CompletableFuture<IRecord<UNIT>> getBestRecord(IStatistic<UNIT> statistic);

    <UNIT extends IStatisticUnit> CompletableFuture<UNIT> getAverage(IStatistic<UNIT> statistic);

    <UNIT extends IStatisticUnit> CompletableFuture<UNIT> getPercentile(IStatistic<UNIT> statistic, double percentile);

    <UNIT extends IStatisticUnit> CompletableFuture<UNIT> getMedian(IStatistic<UNIT> statistic);
}
