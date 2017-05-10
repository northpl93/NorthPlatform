package pl.north93.zgame.api.bukkit.scoreboard.impl;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;

class ScoreboardContextImpl implements IScoreboardContext
{
    private final Player             player;
    private final IScoreboardLayout  layout;
    private final Map<String, Object> data;

    public ScoreboardContextImpl(final Player player, final IScoreboardLayout layout)
    {
        this.player = player;
        this.layout = layout;
        this.data = new HashMap<>();
    }

    @Override
    public Player getPlayer()
    {
        return this.player;
    }

    @Override
    public IScoreboardLayout getLayout()
    {
        return this.layout;
    }

    @Override
    public void set(final String key, final Object value)
    {
        this.data.put(key, value);
    }

    @Override
    public void set(final Map<String, Object> data)
    {
        this.data.putAll(data);
    }

    @Override
    public <T> T get(final String key)
    {
        //noinspection unchecked
        return (T) this.data.get(key);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).append("layout", this.layout).append("data", this.data).toString();
    }
}
