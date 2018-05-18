package pl.north93.zgame.api.bukkit.player.event;

import org.bukkit.event.Event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.player.INorthPlayer;

/**
 * Odpowiednik {@link org.bukkit.event.player.PlayerEvent} przechowujący
 * instancję {@link INorthPlayer}.
 */
public abstract class NorthPlayerEvent extends Event
{
    protected final INorthPlayer who;

    public NorthPlayerEvent(final INorthPlayer who)
    {
        this.who = who;
    }

    public final INorthPlayer getPlayer()
    {
        return this.who;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("who", this.who).toString();
    }
}
