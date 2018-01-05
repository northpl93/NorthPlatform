package pl.north93.zgame.antycheat.analysis.impl;

import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.analysis.timeline.TimelineAnalyser;
import pl.north93.zgame.antycheat.analysis.timeline.TimelineAnalyserConfig;
import pl.north93.zgame.antycheat.timeline.Tick;
import pl.north93.zgame.antycheat.timeline.Timeline;
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

    public SingleAnalysisResult tryFire(final Timeline timeline, final Tick currentTick)
    {
        if (! this.shouldFire(currentTick))
        {
            return null;
        }

        final TimelineWalker walker = timeline.createWalkerForScope(this.config.getScope());
        return this.timelineAnalyser.analyse(timeline.getOwner(), timeline, walker);
    }

    private boolean shouldFire(final Tick currentTick)
    {
        final TimelineAnalyserConfig.Scope scope = this.config.getScope();
        if (scope == TimelineAnalyserConfig.Scope.TICK)
        {
            return true;
        }
        else if (scope == TimelineAnalyserConfig.Scope.SECOND)
        {
            if (currentTick.getTickId() % ONE_SECOND == 0)
            {
                return true;
            }
        }
        else if (scope == TimelineAnalyserConfig.Scope.ALL)
        {
            if (currentTick.getTickId() % (10 * ONE_SECOND) == 0)
            {
                return true;
            }
        }

        return false;
    }
}
