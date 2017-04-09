package pl.arieals.skyblock.quests.shared.api;

import java.util.List;
import java.util.UUID;

public interface IQuest
{
    UUID getId();

    String getName();

    /**
     * Returns list of statistics tracked by this quest and theirs objectives.
     *
     * @return list of {@link ITrackedStatistic}
     */
    List<ITrackedStatistic> getTrackedStatistics();
}
