package pl.north93.northplatform.antycheat.timeline.impl;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import pl.north93.northplatform.antycheat.timeline.DataKey;
import pl.north93.northplatform.antycheat.timeline.PlayerData;
import pl.north93.northplatform.antycheat.timeline.Timeline;

class PlayerDataImpl implements PlayerData
{
    private final Player       player;
    private final TimelineImpl timeline;
    private final Map<DataKey, Object> data = new HashMap<>(2, 0.01f);

    public PlayerDataImpl(final Player player, final TimelineImpl timeline)
    {
        this.player = player;
        this.timeline = timeline;
    }

    @Override
    public Player getPlayer()
    {
        return this.player;
    }

    @Override
    public Timeline getTimeline()
    {
        return this.timeline;
    }

    @Override
    public <T> T get(final DataKey<T> dataKey)
    {
        //noinspection unchecked
        return (T) this.data.computeIfAbsent(dataKey, dk -> dk.getCreator().apply(this));
    }

    @Override
    public <T> void reset(final DataKey<T> dataKey)
    {
        this.data.remove(dataKey);
    }
}
