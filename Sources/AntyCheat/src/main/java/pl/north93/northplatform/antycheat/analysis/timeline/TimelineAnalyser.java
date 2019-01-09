package pl.north93.northplatform.antycheat.analysis.timeline;

import pl.north93.northplatform.antycheat.analysis.SingleAnalysisResult;
import pl.north93.northplatform.antycheat.timeline.PlayerData;
import pl.north93.northplatform.antycheat.timeline.TimelineWalker;

public interface TimelineAnalyser
{
    void configure(TimelineAnalyserConfig config);

    SingleAnalysisResult analyse(PlayerData data, TimelineWalker timelineWalker);
}
