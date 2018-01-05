package pl.north93.zgame.antycheat.event.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import pl.north93.zgame.antycheat.event.AbstractTimelineEvent;

public class PlayerMoveTimelineEvent extends AbstractTimelineEvent
{
    private final Location from;
    private final boolean  fromOnGround;
    private final Location to;
    private final boolean  toOnGround;

    public PlayerMoveTimelineEvent(final Player player, final Location from, final boolean fromOnGround, final Location to, final boolean toOnGround)
    {
        super(player);
        this.from = from;
        this.fromOnGround = fromOnGround;
        this.to = to;
        this.toOnGround = toOnGround;
    }

    public Location getFrom()
    {
        return this.from;
    }

    public boolean isFromOnGround()
    {
        return this.fromOnGround;
    }

    public Location getTo()
    {
        return this.to;
    }

    public boolean isToOnGround()
    {
        return this.toOnGround;
    }
}
