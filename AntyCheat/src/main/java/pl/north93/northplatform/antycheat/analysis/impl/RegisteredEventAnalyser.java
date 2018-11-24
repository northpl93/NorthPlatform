package pl.north93.northplatform.antycheat.analysis.impl;


import javax.annotation.Nonnull;

import java.util.Set;

import co.aikar.timings.Timing;
import lombok.ToString;
import pl.north93.northplatform.antycheat.analysis.event.EventAnalyser;
import pl.north93.northplatform.antycheat.analysis.event.EventAnalyserConfig;
import pl.north93.northplatform.antycheat.utils.AntyCheatTimings;
import pl.north93.northplatform.antycheat.analysis.SingleAnalysisResult;
import pl.north93.northplatform.antycheat.timeline.PlayerData;
import pl.north93.northplatform.antycheat.timeline.PlayerTickInfo;
import pl.north93.northplatform.antycheat.timeline.TimelineEvent;

@ToString
/*default*/ class RegisteredEventAnalyser implements Comparable<RegisteredEventAnalyser>
{
    private final String                       name;
    private final EventAnalyser<TimelineEvent> eventAnalyser;
    private final EventAnalyserConfig          config;

    public RegisteredEventAnalyser(final EventAnalyser<TimelineEvent> eventAnalyser)
    {
        this.name = eventAnalyser.getClass().getSimpleName();
        this.eventAnalyser = eventAnalyser;
        this.config = new EventAnalyserConfig();

        eventAnalyser.configure(this.config);
    }

    public String getName()
    {
        return this.name;
    }

    public SingleAnalysisResult tryFire(final PlayerData data, final PlayerTickInfo playerTickInfo, final TimelineEvent event)
    {
        if (! this.shouldFire(event))
        {
            return null;
        }

        try (final Timing timing = AntyCheatTimings.eventAnalyserTiming(this.getName()))
        {
            // przekazujemy sterowanie do metody analizujacej event i mierzymy czas wykonywania
            return this.eventAnalyser.analyse(data, playerTickInfo, event);
        }
    }

    private boolean shouldFire(final TimelineEvent event)
    {
        final Set<Class<?>> eventWhitelist = this.config.getEventWhitelist();
        if (eventWhitelist.isEmpty())
        {
            return true;
        }

        final Class<? extends TimelineEvent> eventClass = event.getClass();
        for (final Class<?> aClass : eventWhitelist)
        {
            if (aClass.isAssignableFrom(eventClass))
            {
                return true;
            }
        }

        return false;
    }

    private Class<? extends EventAnalyser> getAnalyserClass()
    {
        return this.eventAnalyser.getClass();
    }

    @Override
    public int compareTo(@Nonnull final RegisteredEventAnalyser o)
    {
        return this.config.getOrder() - o.config.getOrder();
    }
}
