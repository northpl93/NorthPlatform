package pl.north93.zgame.antycheat.event;

import java.time.Instant;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.antycheat.timeline.TimelineEvent;

public abstract class AbstractTimelineEvent implements TimelineEvent
{
    protected final Player  player;
    protected final Instant time;

    public AbstractTimelineEvent(final Player player)
    {
        this.player = player;
        this.time = Instant.now();
    }

    @Override
    public Player getOwner()
    {
        return this.player;
    }

    @Override
    public Instant getCreationTime()
    {
        return this.time;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).append("time", this.time).toString();
    }
}
