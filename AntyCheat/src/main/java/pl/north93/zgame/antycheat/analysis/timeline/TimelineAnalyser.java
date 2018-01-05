package pl.north93.zgame.antycheat.analysis.timeline;

import org.bukkit.entity.Player;

import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.timeline.Timeline;
import pl.north93.zgame.antycheat.timeline.TimelineWalker;

public interface TimelineAnalyser
{
    void configure(TimelineAnalyserConfig config);

    SingleAnalysisResult analyse(Player player, Timeline timeline, TimelineWalker timelineWalker);
}
