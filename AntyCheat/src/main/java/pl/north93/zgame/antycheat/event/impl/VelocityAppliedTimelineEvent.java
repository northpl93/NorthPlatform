package pl.north93.zgame.antycheat.event.impl;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.antycheat.event.AbstractTimelineEvent;

public class VelocityAppliedTimelineEvent extends AbstractTimelineEvent
{
    private final Vector velocity;

    public VelocityAppliedTimelineEvent(final Player player, final Vector velocity)
    {
        super(player);
        this.velocity = velocity;
    }

    public Vector getVelocity()
    {
        return this.velocity;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("velocity", this.velocity).toString();
    }
}
