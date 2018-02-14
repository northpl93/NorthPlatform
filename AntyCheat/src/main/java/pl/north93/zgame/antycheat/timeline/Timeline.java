package pl.north93.zgame.antycheat.timeline;

import java.util.Collection;

import org.bukkit.entity.Player;

import pl.north93.zgame.antycheat.analysis.timeline.TimelineAnalyserConfig;

public interface Timeline
{
    PlayerData getData();

    default Player getOwner()
    {
        return this.getData().getPlayer();
    }

    int getTrackedTicks();

    TimelineWalker createWalkerForTick(Tick tick);

    TimelineWalker createWalkerForScope(TimelineAnalyserConfig.Scope scope);

    PlayerTickInfo getCurrentPlayerTickInfo();

    PlayerTickInfo getPlayerTickInfo(Tick tick);

    PlayerTickInfo getPreviousPlayerTickInfo(Tick tick, int previous);

    Collection<TimelineEvent> getEvents();

    Collection<TimelineEvent> getEvents(Tick tick);

    <T extends TimelineEvent> T getPreviousEvent(Class<T> classEvent);
}
