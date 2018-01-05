package pl.north93.zgame.antycheat.cheat.movement;

import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyser;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyserConfig;
import pl.north93.zgame.antycheat.event.impl.PlayerMoveTimelineEvent;
import pl.north93.zgame.antycheat.timeline.PlayerData;
import pl.north93.zgame.antycheat.timeline.PlayerTickInfo;

public class MovementManipulationChecker implements EventAnalyser<PlayerMoveTimelineEvent>
{

    @Override
    public void configure(final EventAnalyserConfig config)
    {
        config.whitelistEvent(PlayerMoveTimelineEvent.class);
    }

    @Override
    public SingleAnalysisResult analyse(final PlayerData data, final PlayerTickInfo tickInfo, final PlayerMoveTimelineEvent event)
    {

        return null;
    }
}
