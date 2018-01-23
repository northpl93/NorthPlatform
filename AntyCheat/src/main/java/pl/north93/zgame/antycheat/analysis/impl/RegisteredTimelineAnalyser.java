package pl.north93.zgame.antycheat.analysis.impl;

import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.analysis.timeline.TimelineAnalyser;
import pl.north93.zgame.antycheat.analysis.timeline.TimelineAnalyserConfig;
import pl.north93.zgame.antycheat.timeline.PlayerData;
import pl.north93.zgame.antycheat.timeline.Tick;
import pl.north93.zgame.antycheat.timeline.TimelineWalker;

/*default*/ class RegisteredTimelineAnalyser
{
    private static final int ONE_SECOND = 20;
    private final TimelineAnalyser       timelineAnalyser;
    private final TimelineAnalyserConfig config;

    public RegisteredTimelineAnalyser(final TimelineAnalyser timelineAnalyser)
    {
        this.timelineAnalyser = timelineAnalyser;
        this.config = new TimelineAnalyserConfig();

        timelineAnalyser.configure(this.config);
    }

    public SingleAnalysisResult tryFire(final PlayerData data, final Tick currentTick)
    {
        if (! this.shouldFire(currentTick))
        {
            return null;
        }

        final TimelineWalker walker = data.getTimeline().createWalkerForScope(this.config.getScope());
        return this.timelineAnalyser.analyse(data, walker);
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
