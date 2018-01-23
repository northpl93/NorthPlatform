package pl.north93.zgame.antycheat.analysis.impl;

import javax.annotation.Nonnull;

import java.util.Set;

import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyser;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyserConfig;
import pl.north93.zgame.antycheat.timeline.PlayerData;
import pl.north93.zgame.antycheat.timeline.PlayerTickInfo;
import pl.north93.zgame.antycheat.timeline.TimelineEvent;

/*default*/ class RegisteredEventAnalyser implements Comparable<RegisteredEventAnalyser>
{
    private final EventAnalyser<TimelineEvent> eventAnalyser;
    private final EventAnalyserConfig          config;

    public RegisteredEventAnalyser(final EventAnalyser<TimelineEvent> eventAnalyser)
    {
        this.eventAnalyser = eventAnalyser;
        this.config = new EventAnalyserConfig();

        eventAnalyser.configure(this.config);
    }

    public SingleAnalysisResult tryFire(final PlayerData data, final PlayerTickInfo playerTickInfo, final TimelineEvent event)
    {
        if (! this.shouldFire(event))
        {
            return null;
        }

        return this.eventAnalyser.analyse(data, playerTickInfo, event);
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
        if (this.config.getFireBefore().contains(o.getAnalyserClass()))
        {
            return -1;
        }

        if (o.config.getFireBefore().contains(this.getAnalyserClass()))
        {
            return 1;
        }

        return 0;
    }
}
