package pl.arieals.skyblock.quests.shared.impl.statistics;

import java.util.Locale;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.skyblock.quests.shared.api.IObjective;
import pl.arieals.skyblock.quests.shared.api.StatisticType;

public class MobKillStatistic extends AbstractStatistic
{
    private String mobType;

    MobKillStatistic() // for serialization
    {
        super(null, null);
    }

    MobKillStatistic(final String mobType, final IObjective objective)
    {
        super(StatisticType.MOB_KILL, objective);
        this.mobType = mobType;
    }

    @Override
    public String getKey()
    {
        return "mobkill." + this.mobType.toLowerCase(Locale.ENGLISH);
    }

    public String getMobType()
    {
        return this.mobType;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("mobType", this.mobType).toString();
    }
}
