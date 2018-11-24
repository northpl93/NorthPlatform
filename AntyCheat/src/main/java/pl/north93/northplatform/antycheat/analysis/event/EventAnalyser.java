package pl.north93.northplatform.antycheat.analysis.event;

import pl.north93.northplatform.antycheat.analysis.SingleAnalysisResult;
import pl.north93.northplatform.antycheat.timeline.PlayerData;
import pl.north93.northplatform.antycheat.timeline.PlayerTickInfo;
import pl.north93.northplatform.antycheat.timeline.TimelineEvent;

public interface EventAnalyser<T extends TimelineEvent>
{
    void configure(EventAnalyserConfig config);

    SingleAnalysisResult analyse(PlayerData data, PlayerTickInfo tickInfo, T event);
}
