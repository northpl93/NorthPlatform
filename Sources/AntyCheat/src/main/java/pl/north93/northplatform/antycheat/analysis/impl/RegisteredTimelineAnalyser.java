package pl.north93.northplatform.antycheat.analysis.impl;


import co.aikar.timings.Timing;
import pl.north93.northplatform.antycheat.utils.AntyCheatTimings;
import pl.north93.northplatform.antycheat.analysis.SingleAnalysisResult;
import pl.north93.northplatform.antycheat.analysis.timeline.TimelineAnalyser;
import pl.north93.northplatform.antycheat.analysis.timeline.TimelineAnalyserConfig;
import pl.north93.northplatform.antycheat.timeline.PlayerData;
import pl.north93.northplatform.antycheat.timeline.Tick;
import pl.north93.northplatform.antycheat.timeline.TimelineWalker;

/*default*/ class RegisteredTimelineAnalyser
{
    private static final int ONE_SECOND = 20;
    private final String                 name;
    private final TimelineAnalyser       timelineAnalyser;
    private final TimelineAnalyserConfig config;

    public RegisteredTimelineAnalyser(final TimelineAnalyser timelineAnalyser)
    {
        this.name = timelineAnalyser.getClass().getSimpleName();
        this.timelineAnalyser = timelineAnalyser;
        this.config = new TimelineAnalyserConfig();

        timelineAnalyser.configure(this.config);
    }

    public String getName()
    {
        return this.name;
    }

    public SingleAnalysisResult tryFire(final PlayerData data, final Tick currentTick)
    {
        if (! this.shouldFire(currentTick))
        {
            return null;
        }

        final TimelineWalker walker = data.getTimeline().createWalkerForScope(this.config.getScope());
        try (final Timing timing = AntyCheatTimings.timelineAnalyserTiming(this.getName()))
        {
            // przekazujemy sterowanie do metody analizujacej linie czasu i mierzymy czas wykonywania
            return this.timelineAnalyser.analyse(data, walker);
        }
    }

    private boolean shouldFire(final Tick currentTick)
    {
        switch (this.config.getScope())
        {
            case TICK:
                return true;
            case SECOND:
                return currentTick.getTickId() % ONE_SECOND == 0;
            case FIVE_SECONDS:
                return currentTick.getTickId() % (5 * ONE_SECOND) == 0;
            case ALL:
                return currentTick.getTickId() % (10 * ONE_SECOND) == 0;
            default:
                throw new IllegalStateException(this.config.getScope().name());
        }
    }
}
