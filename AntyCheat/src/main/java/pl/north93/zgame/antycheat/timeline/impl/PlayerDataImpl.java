package pl.north93.zgame.antycheat.timeline.impl;

import static org.diorite.utils.SimpleEnum.SMALL_LOAD_FACTOR;


import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import pl.north93.zgame.antycheat.timeline.DataKey;
import pl.north93.zgame.antycheat.timeline.PlayerData;

class PlayerDataImpl implements PlayerData
{
    private final Player player;
    private final Map<DataKey, Object> data = new HashMap<>(2, SMALL_LOAD_FACTOR);

    public PlayerDataImpl(final Player player)
    {
        this.player = player;
    }

    @Override
    public Player getPlayer()
    {
        return this.player;
    }

    @Override
    public <T> T get(final DataKey<T> dataKey)
    {
        //noinspection unchecked
        return (T) this.data.computeIfAbsent(dataKey, dk -> dk.getCreator().get());
    }

    @Override
    public <T> void reset(final DataKey<T> dataKey)
    {
        this.data.remove(dataKey);
    }
}
