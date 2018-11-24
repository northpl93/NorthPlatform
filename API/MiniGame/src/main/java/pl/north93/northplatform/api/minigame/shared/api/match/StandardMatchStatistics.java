package pl.north93.northplatform.api.minigame.shared.api.match;

import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.type.LongerTimeBetterStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.unit.DurationUnit;

public interface StandardMatchStatistics
{
    IStatistic<DurationUnit> MATCH_DURATION = new LongerTimeBetterStatistic("match/duration");
}
