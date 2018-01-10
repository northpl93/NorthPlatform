package pl.north93.zgame.antycheat.cheat.movement.check;

import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyser;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyserConfig;
import pl.north93.zgame.antycheat.event.impl.ClientMoveTimelineEvent;
import pl.north93.zgame.antycheat.timeline.PlayerData;
import pl.north93.zgame.antycheat.timeline.PlayerTickInfo;

public class MovementManipulationChecker implements EventAnalyser<ClientMoveTimelineEvent>
{

    @Override
    public void configure(final EventAnalyserConfig config)
    {
        config.whitelistEvent(ClientMoveTimelineEvent.class);
    }

    @Override
    public SingleAnalysisResult analyse(final PlayerData data, final PlayerTickInfo tickInfo, final ClientMoveTimelineEvent event)
    {



        return null;
    }
}
