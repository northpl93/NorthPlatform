package pl.north93.northplatform.antycheat.analysis.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.antycheat.analysis.reaction.ITriggerLevel;
import pl.north93.northplatform.antycheat.analysis.reaction.IViolationMonitor;
import pl.north93.northplatform.antycheat.analysis.reaction.TriggerCheckContext;
import pl.north93.northplatform.antycheat.analysis.reaction.TriggerCondition;
import pl.north93.northplatform.antycheat.analysis.Violation;

/*default*/ class ViolationMonitorImpl implements IViolationMonitor
{
    private final Violation          violation;
    private final List<TriggerLevel> levels;
    private final int                ticks;

    public ViolationMonitorImpl(final Violation violation, final int ticks)
    {
        this.violation = violation;
        this.levels = new ArrayList<>(2);
        this.ticks = ticks;
    }

    @Override
    public Violation getViolation()
    {
        return this.violation;
    }

    @Override
    public int getTicks()
    {
        return this.ticks;
    }

    @Override
    public ITriggerLevel addLevel(final TriggerCondition condition)
    {
        final TriggerLevel level = new TriggerLevel(condition);
        this.levels.add(level);
        return level;
    }

    public Map<TriggerLevel, Boolean> checkLevels(final TriggerCheckContext context)
    {
        final Map<TriggerLevel, Boolean> map = new HashMap<>();

        for (final TriggerLevel level : this.levels)
        {
            map.put(level, level.checkCondition(context));
        }

        return map;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("violation", this.violation).append("levels", this.levels).append("ticks", this.ticks).toString();
    }
}
