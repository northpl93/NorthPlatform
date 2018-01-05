package pl.north93.zgame.antycheat.event.impl;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.antycheat.event.AbstractTimelineEvent;

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

    public String getChannel()
    {
        return this.channel;
    }

    public byte[] getData()
    {
        return this.data;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("channel", this.channel).append("data", this.data).toString();
    }
}
