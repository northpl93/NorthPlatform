package pl.arieals.skyblock.quests.shared.impl.statistics;

import pl.arieals.skyblock.quests.shared.api.IObjective;
import pl.arieals.skyblock.quests.shared.api.ITrackedStatistic;

public final class StatisticsFactory
{
    public static final StatisticsFactory INSTANCE = new StatisticsFactory();

    public ITrackedStatistic mobKill(final String mobType, final IObjective objective)
    {
        return new MobKillStatistic(mobType, objective);
    }
}
