package pl.north93.northplatform.antycheat.timeline;

import java.util.Collection;

import org.bukkit.entity.Player;

import pl.north93.northplatform.antycheat.analysis.timeline.TimelineAnalyserConfig;

public interface Timeline
{
    PlayerData getData();

    default Player getOwner()
    {
        return this.getData().getPlayer();
    }

    int getTrackedTicks();

    PlayerTickInfo getCurrentPlayerTickInfo();

    PlayerTickInfo getPlayerTickInfo(Tick tick);

    PlayerTickInfo getPreviousPlayerTickInfo(Tick tick, int previous);

    Collection<PlayerTickInfo> getAllTicks();

    TimelineWalker createWalkerForTick(Tick tick);

    TimelineWalker createWalkerForScope(TimelineAnalyserConfig.Scope scope);

    Collection<TimelineEvent> getEvents();

    Collection<TimelineEvent> getEvents(Tick tick);

    <T extends TimelineEvent> T getPreviousEvent(Class<T> classEvent);
}
