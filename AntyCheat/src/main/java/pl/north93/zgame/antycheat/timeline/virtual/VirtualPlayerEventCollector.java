package pl.north93.zgame.antycheat.timeline.virtual;

import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyser;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyserConfig;
import pl.north93.zgame.antycheat.event.impl.ClientMoveTimelineEvent;
import pl.north93.zgame.antycheat.event.impl.EntityActionTimelineEvent;
import pl.north93.zgame.antycheat.timeline.PlayerData;
import pl.north93.zgame.antycheat.timeline.PlayerTickInfo;
import pl.north93.zgame.antycheat.timeline.TimelineEvent;

/*default*/ class VirtualPlayerEventCollector implements EventAnalyser<TimelineEvent>
{
    @Override
    public void configure(final EventAnalyserConfig config)
    {
        config.order(-10);
        config.whitelistEvent(ClientMoveTimelineEvent.class);
        config.whitelistEvent(EntityActionTimelineEvent.class);
    }

    @Override
    public SingleAnalysisResult analyse(final PlayerData data, final PlayerTickInfo tickInfo, final TimelineEvent event)
    {
        final VirtualPlayerImpl virtualPlayer = (VirtualPlayerImpl) VirtualPlayer.get(data);

        if (event instanceof ClientMoveTimelineEvent)
        {
            final ClientMoveTimelineEvent clientMoveTimelineEvent = (ClientMoveTimelineEvent) event;
            virtualPlayer.updateLocation(clientMoveTimelineEvent.getTo());
        }
        else if (event instanceof EntityActionTimelineEvent)
        {
            final EntityActionTimelineEvent entityActionTimelineEvent = (EntityActionTimelineEvent) event;
            switch (entityActionTimelineEvent.getAction())
            {
                case START_SPRINTING:
                    virtualPlayer.updateSprinting(true);
                    break;
                case STOP_SPRINTING:
                    virtualPlayer.updateSprinting(false);
                    break;
            }
        }

        return SingleAnalysisResult.EMPTY;
    }
}
