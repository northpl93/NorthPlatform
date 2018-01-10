package pl.north93.zgame.antycheat.event.impl;

import org.bukkit.entity.Player;

import pl.north93.zgame.antycheat.event.AbstractTimelineEvent;
import pl.north93.zgame.antycheat.utils.location.RichEntityLocation;

public class ClientMoveTimelineEvent extends AbstractTimelineEvent
{
    private final RichEntityLocation from;
    private final boolean            fromOnGround;
    private final RichEntityLocation to;
    private final boolean            toOnGround;

    public ClientMoveTimelineEvent(final Player player, final RichEntityLocation from, final boolean fromOnGround, final RichEntityLocation to, final boolean toOnGround)
    {
        super(player);
        this.from = from;
        this.fromOnGround = fromOnGround;
        this.to = to;
        this.toOnGround = toOnGround;
    }

    public RichEntityLocation getFrom()
    {
        return this.from;
    }

    public boolean isFromOnGround()
    {
        return this.fromOnGround;
    }

    public RichEntityLocation getTo()
    {
        return this.to;
    }

    public boolean isToOnGround()
    {
        return this.toOnGround;
    }
}
