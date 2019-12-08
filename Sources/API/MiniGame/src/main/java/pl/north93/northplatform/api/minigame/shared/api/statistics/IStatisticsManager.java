package pl.north93.northplatform.api.minigame.shared.api.statistics;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IStatisticsManager
{
    IStatisticHolder getHolder(HolderIdentity holder);

    default IStatisticHolder getPlayerHolder(final UUID playerId)
    {
        return this.getHolder(new HolderIdentity("player", playerId));
    }

    <T, UNIT extends IStatisticUnit<T>> CompletableFuture<IRanking<T, UNIT>> getRanking(IStatistic<T, UNIT> statistic, int size, IStatisticFilter... filters);

    <T, UNIT extends IStatisticUnit<T>> CompletableFuture<IRecord<T, UNIT>> getRecord(IStatistic<T, UNIT> statistic, IStatisticFilter... filters);

    <T, UNIT extends IStatisticUnit<T>> CompletableFuture<UNIT> getAverage(IStatistic<T, UNIT> statistic);

    <T, UNIT extends IStatisticUnit<T>> CompletableFuture<UNIT> getPercentile(IStatistic<T, UNIT> statistic, double percentile);

    <T, UNIT extends IStatisticUnit<T>> CompletableFuture<UNIT> getMedian(IStatistic<T, UNIT> statistic);
}
