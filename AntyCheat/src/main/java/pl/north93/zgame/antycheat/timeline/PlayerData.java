package pl.north93.zgame.antycheat.timeline;

import org.bukkit.entity.Player;

public interface PlayerData
{
    Player getPlayer();

    Timeline getTimeline();

    default PlayerTickInfo getPlayerTickInfo(final Tick tick)
    {
        return this.getTimeline().getPlayerTickInfo(tick);
    }

    default PlayerTickInfo getPreviousPlayerTickInfo(final Tick tick, final int previous)
    {
        return this.getTimeline().getPreviousPlayerTickInfo(tick, previous);
    }

    <T> T get(DataKey<T> dataKey);

    <T> void reset(DataKey<T> dataKey);
}
