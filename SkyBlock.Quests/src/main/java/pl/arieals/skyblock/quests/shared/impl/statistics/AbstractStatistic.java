package pl.arieals.skyblock.quests.shared.impl.statistics;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.skyblock.quests.shared.api.IObjective;
import pl.arieals.skyblock.quests.shared.api.ITrackedStatistic;
import pl.arieals.skyblock.quests.shared.api.StatisticType;

abstract class AbstractStatistic implements ITrackedStatistic
{
    private StatisticType statisticType;
    private IObjective    objective;

    public AbstractStatistic(final StatisticType statisticType, final IObjective objective)
    {
        this.statisticType = statisticType;
        this.objective = objective;
    }

    @Override
    public StatisticType getStatisticType()
    {
        return this.statisticType;
    }

    @Override
    public IObjective getObjective()
    {
        return this.objective;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("statisticType", this.statisticType).append("objective", this.objective).toString();
    }
}
