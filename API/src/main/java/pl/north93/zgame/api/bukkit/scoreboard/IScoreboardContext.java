package pl.north93.zgame.api.bukkit.scoreboard;

import java.util.Map;

import org.bukkit.entity.Player;

public interface IScoreboardContext
{
    Player getPlayer();

    IScoreboardLayout getLayout();

    void set(String key, Object value);

    void set(Map<String, Object> data);

    <T> T get(String key);

    void update(); // force update
}
