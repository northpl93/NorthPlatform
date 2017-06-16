package pl.arieals.api.minigame.shared.api.statistics;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IStatistic<E extends IStatisticEncoder>
{
    String getKey();

    boolean isReverseOrder();

    CompletableFuture<IRecord> getGlobalRecord();

    CompletableFuture<IRecordResult> record(UUID playerId, E value);
}
