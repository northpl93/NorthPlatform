package pl.north93.zgame.antycheat.event.impl;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.ToString;
import pl.north93.zgame.antycheat.event.AbstractTimelineEvent;

@Getter
@ToString
public class PluginMessageTimelineEvent extends AbstractTimelineEvent
{
    private final String channel;
    private final byte[] data;

    public PluginMessageTimelineEvent(final Player player, final String channel, final byte[] data)
    {
        super(player);
        this.channel = channel;
        this.data = data;
    }
}
