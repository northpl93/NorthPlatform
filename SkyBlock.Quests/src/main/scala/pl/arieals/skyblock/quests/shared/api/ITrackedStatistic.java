package pl.arieals.skyblock.quests.shared.api;

/**
 * Represents a statistic tracked by quest.
 */
public interface ITrackedStatistic
{
    StatisticType getStatisticType();

    IObjective getObjective();

    String getKey();
}
