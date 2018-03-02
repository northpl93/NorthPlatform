package pl.north93.zgame.antycheat.analysis.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult.ViolationEntry;
import pl.north93.zgame.antycheat.analysis.Violation;
import pl.north93.zgame.antycheat.timeline.Tick;

/**
 * Główny obiekt przechowujący historię naruszeń danego gracza.
 */
public class ViolationsStorage
{
    private final AnalysisManager                          analysisManager;
    private final Player                                   player;
    private final Multimap<Tick, ViolationEntry>           violations;
    private final Map<ViolationMonitorImpl, MonitorStatus> monitors;

    public ViolationsStorage(final AnalysisManager analysisManager, final Player player)
    {
        this.analysisManager = analysisManager;
        this.player = player;
        this.violations = LinkedHashMultimap.create();
        this.monitors = new HashMap<>();
    }

    public void recordAnalysisResult(final Tick tick, final SingleAnalysisResult analysisResult)
    {
        for (final ViolationEntry entry : analysisResult.getViolations())
        {
            this.violations.put(tick, entry);
        }
    }

    public Collection<ViolationEntry> getViolationsFromTicks(final Violation violation, final Tick currentTick, final int amount)
    {
        final int minTickId = currentTick.getTickId() - amount;
        return this.violations.entries().stream()
                              .filter(this.violationsFilter(violation, minTickId))
                              .map(Map.Entry::getValue)
                              .collect(Collectors.toList());
    }

    public void cleanUpOldViolations(final Tick currentTick)
    {
        final int ticksToKeep = this.analysisManager.getTicksToKeep();
        if (currentTick.getTickId() - ticksToKeep < 0)
        {
            return;
        }

        final Tick tickToRemove = this.analysisManager.getPreviousTick(currentTick, ticksToKeep);
        this.violations.removeAll(tickToRemove);
    }

    public MonitorStatus getMonitorStatus(final ViolationMonitorImpl monitor)
    {
        return this.monitors.computeIfAbsent(monitor, m -> new MonitorStatus(this.player, m));
    }

    // generuje filtr używany do zwrócenia listy ViolationEntry
    private Predicate<Map.Entry<Tick, ViolationEntry>> violationsFilter(final Violation violation, final int minTickId)
    {
        return entry ->
        {
            final Tick key = entry.getKey();
            final ViolationEntry violationEntry = entry.getValue();

            return violationEntry.getViolation() == violation && key.getTickId() >= minTickId;
        };
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).toString();
    }
}
