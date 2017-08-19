package pl.arieals.api.minigame.shared.api.statistics;

import java.time.Instant;

/**
 * Reprezentuje zapisana wartosc danej statystyki dla danego holdera.
 */
public interface IRecord<UNIT extends IStatisticUnit>
{
    IStatistic<UNIT> getStatistic();

    IStatisticHolder getHolder();

    Instant getRecordedAt();

    UNIT getValue();
}
