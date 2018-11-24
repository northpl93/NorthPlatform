package pl.north93.northplatform.antycheat.event.impl;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.ToString;
import pl.north93.northplatform.antycheat.event.AbstractTimelineEvent;
import pl.north93.northplatform.antycheat.utils.location.RichEntityLocation;

@Getter
@ToString
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
}
