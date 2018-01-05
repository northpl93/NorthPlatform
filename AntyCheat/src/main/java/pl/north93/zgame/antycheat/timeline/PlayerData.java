package pl.north93.zgame.antycheat.timeline;

import org.bukkit.entity.Player;

public interface PlayerData
{
    Player getPlayer();

    <T> T get(DataKey<T> dataKey);

    <T> void reset(DataKey<T> dataKey);
}
