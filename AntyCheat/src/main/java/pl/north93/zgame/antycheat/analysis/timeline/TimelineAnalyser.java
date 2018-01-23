package pl.north93.zgame.antycheat.analysis.timeline;

import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.timeline.PlayerData;
import pl.north93.zgame.antycheat.timeline.TimelineWalker;

public interface TimelineAnalyser
{
    void configure(TimelineAnalyserConfig config);

    SingleAnalysisResult analyse(PlayerData data, TimelineWalker timelineWalker);
}
