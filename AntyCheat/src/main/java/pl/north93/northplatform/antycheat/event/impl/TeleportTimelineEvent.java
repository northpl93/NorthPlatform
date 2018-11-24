package pl.north93.northplatform.antycheat.event.impl;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.ToString;
import pl.north93.northplatform.antycheat.event.AbstractTimelineEvent;
import pl.north93.northplatform.antycheat.utils.location.RichEntityLocation;

@Getter
@ToString
public class TeleportTimelineEvent extends AbstractTimelineEvent
{
    private final RichEntityLocation from;
    private final RichEntityLocation to;

    public TeleportTimelineEvent(final Player player, final RichEntityLocation from, final RichEntityLocation to)
    {
        super(player);
        this.from = from;
        this.to = to;
    }
}
