package pl.north93.zgame.antycheat.analysis.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import pl.north93.zgame.antycheat.analysis.FalsePositiveProbability;
import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyser;
import pl.north93.zgame.antycheat.analysis.timeline.TimelineAnalyser;
import pl.north93.zgame.antycheat.timeline.PlayerData;
import pl.north93.zgame.antycheat.timeline.PlayerTickInfo;
import pl.north93.zgame.antycheat.timeline.Tick;
import pl.north93.zgame.antycheat.timeline.Timeline;
import pl.north93.zgame.antycheat.timeline.TimelineEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;

public class AnalysisManager
{
    private final List<RegisteredEventAnalyser>    eventAnalysers = new ArrayList<>();
    private final List<RegisteredTimelineAnalyser> timelineAnalysers = new ArrayList<>();

    @Bean
    private AnalysisManager()
    {
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
                this.print(analysisResult);
                // todo zrobic cos z tym analysis result
            }
        }
    }

    private void fireTimelineAnalysers(final Timeline timeline, final Tick currentTick)
    {
        final PlayerData data = timeline.getData();

        for (final RegisteredTimelineAnalyser timelineAnalyser : this.timelineAnalysers)
        {
            final SingleAnalysisResult analysisResult = timelineAnalyser.tryFire(data, currentTick);
            this.print(analysisResult);
            // todo zrobic cos z tym analysis result
        }
    }

    private void print(final SingleAnalysisResult singleAnalysisResult)
    {
        if (singleAnalysisResult == null)
        {
            return;
        }

        final Collection<SingleAnalysisResult.ViolationEntry> violations = singleAnalysisResult.getViolations();
        for (final SingleAnalysisResult.ViolationEntry violation : violations)
        {
            if (violation.getFalsePositiveProbability() == FalsePositiveProbability.DEFINITELY)
            {
                // ukrywamy definitely
                continue;
            }
            Bukkit.broadcastMessage(ChatColor.RED + "Violation:" + ChatColor.YELLOW + violation.getViolation().name() + ChatColor.RED + " FalsePositiveP:" + ChatColor.YELLOW + violation.getFalsePositiveProbability());
            Bukkit.broadcastMessage(ChatColor.RED + "Desc:" + ChatColor.GRAY + violation.getDescription());
        }
    }
}

