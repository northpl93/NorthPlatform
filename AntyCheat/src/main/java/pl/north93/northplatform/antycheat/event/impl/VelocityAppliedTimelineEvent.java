package pl.north93.northplatform.antycheat.event.impl;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import lombok.Getter;
import lombok.ToString;
import pl.north93.northplatform.antycheat.event.AbstractTimelineEvent;

@Getter
@ToString
public class VelocityAppliedTimelineEvent extends AbstractTimelineEvent
{
    private final Vector velocity;

    public VelocityAppliedTimelineEvent(final Player player, final Vector velocity)
    {
        super(player);
        this.velocity = velocity;
    }
}
