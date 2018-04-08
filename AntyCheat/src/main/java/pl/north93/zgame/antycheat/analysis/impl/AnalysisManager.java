package pl.north93.zgame.antycheat.analysis.impl;

import static java.text.MessageFormat.format;

import static pl.north93.zgame.api.bukkit.utils.chat.ChatUtils.translateAlternateColorCodes;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.lazy.IntLazyValue;

import pl.north93.zgame.antycheat.analysis.FalsePositiveProbability;
import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult.ViolationEntry;
import pl.north93.zgame.antycheat.analysis.Violation;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyser;
import pl.north93.zgame.antycheat.analysis.reaction.TriggerCheckContext;
import pl.north93.zgame.antycheat.analysis.timeline.TimelineAnalyser;
import pl.north93.zgame.antycheat.timeline.PlayerData;
import pl.north93.zgame.antycheat.timeline.PlayerTickInfo;
import pl.north93.zgame.antycheat.timeline.Tick;
import pl.north93.zgame.antycheat.timeline.Timeline;
import pl.north93.zgame.antycheat.timeline.TimelineEvent;
import pl.north93.zgame.antycheat.timeline.impl.TimelineManager;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;

public class AnalysisManager
{
    private final List<ViolationMonitorImpl>       monitors          = new ArrayList<>();
    private final IntLazyValue                     ticksToKeep;
    private final List<RegisteredEventAnalyser>    eventAnalysers    = new ArrayList<>();
    private final List<RegisteredTimelineAnalyser> timelineAnalysers = new ArrayList<>();

    @Bean
    private AnalysisManager()
    {
        this.ticksToKeep = new IntLazyValue(this::calculateTicksToKeep);
    }

    public void registerMonitor(final ViolationMonitorImpl monitor)
    {
        this.monitors.add(monitor);
        this.ticksToKeep.reset();
    }

    public ViolationMonitorImpl createMonitor(final Violation violation, final int ticks)
    {
        final ViolationMonitorImpl monitor = new ViolationMonitorImpl(violation, ticks);
        this.registerMonitor(monitor);
        return monitor;
    }

    @Aggregator(EventAnalyser.class) // automatyczna agregacja
    public void registerEventAnalyser(final EventAnalyser<TimelineEvent> analyser)
    {
        this.eventAnalysers.add(new RegisteredEventAnalyser(analyser));
        Collections.sort(this.eventAnalysers);
    }

    @Aggregator(TimelineAnalyser.class) // automatyczna agregacja
    public void registerTimelineAnalyser(final TimelineAnalyser timelineAnalyser)
    {
        this.timelineAnalysers.add(new RegisteredTimelineAnalyser(timelineAnalyser));
    }

    // metoda pomocnicza przekierowująca do TimelineManager
    public Tick getPreviousTick(final Tick tick, final int previous)
    {
        return this.getTimelineManager().getPreviousTick(tick, previous);
    }

    /**
     * Metoda uruchamiająca analizę danej linii czasu.
     *
     * @param timeline Linia czasu do zanalizowania.
     * @param currentTick Aktualny tick.
     */
    public void fireAnalysis(final Timeline timeline, final Tick currentTick)
    {
        this.fireEventAnalysers(timeline, currentTick);
        this.fireTimelineAnalysers(timeline, currentTick);
    }

    private void fireEventAnalysers(final Timeline timeline, final Tick currentTick)
    {
        final PlayerData data = timeline.getData();
        final PlayerTickInfo tickInfo = timeline.getPlayerTickInfo(currentTick);
        final Collection<TimelineEvent> events = timeline.getEvents(currentTick);

        for (final RegisteredEventAnalyser eventAnalyser : this.eventAnalysers)
        {
            for (final TimelineEvent event : events)
            {
                final SingleAnalysisResult analysisResult = eventAnalyser.tryFire(data, tickInfo, event);
                this.handleAnalysisResult(data.getPlayer(), currentTick, analysisResult);
            }
        }
    }

    private void fireTimelineAnalysers(final Timeline timeline, final Tick currentTick)
    {
        final PlayerData data = timeline.getData();

        for (final RegisteredTimelineAnalyser timelineAnalyser : this.timelineAnalysers)
        {
            final SingleAnalysisResult analysisResult = timelineAnalyser.tryFire(data, currentTick);
            this.handleAnalysisResult(data.getPlayer(), currentTick, analysisResult);
        }
    }

    // zwraca ilość ticków którą muszą rejestrować ViolationsStorage, obliczona z monitorów
    public int getTicksToKeep()
    {
        return this.ticksToKeep.get();
    }

    /**
     * Podsumowuje naruszenia w danym ticku dla gracza.
     *
     * @param player Gracz u którego robimy podsumowanie.
     * @param currentTick Aktualny tick.
     */
    public void summarizeTick(final Player player, final Tick currentTick)
    {
        final ViolationsStorage violations = this.getTimelineManager().getViolations(player);

        // kasuje stare naruszenia
        violations.cleanUpOldViolations(currentTick);

        // aktualizujemy status poziomów
        for (final ViolationMonitorImpl monitor : this.monitors)
        {
            final Collection<ViolationEntry> violationsFromTicks = violations.getViolationsFromTicks(monitor.getViolation(), currentTick, monitor.getTicks());
            final TriggerCheckContext context = new TriggerCheckContext(violationsFromTicks);

            final MonitorStatus status = violations.getMonitorStatus(monitor);

            final Map<TriggerLevel, Boolean> newLevelsStatus = monitor.checkLevels(context);
            this.updateTriggerStatusForLevels(status, newLevelsStatus);
        }
    }

    private void updateTriggerStatusForLevels(final MonitorStatus status, final Map<TriggerLevel, Boolean> newStatus)
    {
        for (final Map.Entry<TriggerLevel, Boolean> entry : newStatus.entrySet())
        {
            final TriggerLevel level = entry.getKey();

            final boolean isTriggered = status.isTriggered(level); // status z poprzedniego ticka
            final boolean shouldBeTriggered = entry.getValue(); // nowy, policzony status

            if (! isTriggered && shouldBeTriggered)
            {
                status.setTriggered(level, true);
                level.fireListeners(status.getPlayer(), true);
            }
            else if (isTriggered && ! shouldBeTriggered)
            {
                status.setTriggered(level, false);
                level.fireListeners(status.getPlayer(), false);
            }
        }
    }

    private void handleAnalysisResult(final Player player, final Tick tick, final SingleAnalysisResult result)
    {
        if (result == null)
        {
            // ignorujemy nullowe wartości które mogą zwrócić analysery
            return;
        }

        // rejestrujemy wyniki analizy w historii
        this.getTimelineManager().pushAnalysisResultForPlayer(player, tick, result);

        // printujemy wiadomość debugowania
        //this.print(player, result);
    }

    private void print(final Player player, final SingleAnalysisResult singleAnalysisResult)
    {
        final Collection<ViolationEntry> violations = singleAnalysisResult.getViolations();
        for (final ViolationEntry violation : violations)
        {
            if (violation.getFalsePositiveProbability() == FalsePositiveProbability.DEFINITELY)
            {
                // ukrywamy definitely
                continue;
            }
            final String nick = player.getName();
            final String violationName = violation.getViolation().name();

            final String firstLine = format("&cP: &e{0} &cV: &e{1} &cFPP: &e{2}", nick, violationName, violation.getFalsePositiveProbability());
            final String secondLine = format("&cD: &7{0}", violation.getDescription());

            Bukkit.broadcastMessage(translateAlternateColorCodes(firstLine));
            Bukkit.broadcastMessage(translateAlternateColorCodes(secondLine));
        }
    }

    private int calculateTicksToKeep()
    {
        return this.monitors.stream().mapToInt(ViolationMonitorImpl::getTicks).max().orElse(0);
    }

    private TimelineManager getTimelineManager()
    {
        return TimelineManagerAccess.getManager();
    }
}

/*default*/ final class TimelineManagerAccess
{
    private static TimelineManagerAccess INSTANCE;
    private final TimelineManager manager;

    @Bean
    private TimelineManagerAccess(final TimelineManager manager)
    {
        this.manager = manager;
        INSTANCE = this;
    }

    public static TimelineManager getManager()
    {
        return INSTANCE.manager;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("manager", this.manager).toString();
    }
}