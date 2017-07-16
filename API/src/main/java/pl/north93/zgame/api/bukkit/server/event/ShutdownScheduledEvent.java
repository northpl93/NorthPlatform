package pl.north93.zgame.api.bukkit.server.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Event wywoluje sie gdy zostanie zaplanowane wylaczenie serwera,
 * a pozniej wykonuje sie cyklicznie dopoki nie uda sie wylaczyc serwera.
 */
public class ShutdownScheduledEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    /**
     * Sprawdza czy wylaczenie serwera zostalo odlozone na pozniej.
     * Jesli tak, ten event zostanie wywolany jeszcze raz.
     * @return czy wylaczenie jest odlozone na pozniej.
     */
    @Override
    public boolean isCancelled()
    {
        return this.cancelled;
    }

    /**
     * Anulowanie tego eventu spowoduje odlozenie zadania na pozniej.
     * Event zostanie wywolany ponownie pozniej.
     * @param b czy wylaczenie jest odlozone na pozniej.
     */
    @Override
    public void setCancelled(final boolean b)
    {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("cancelled", this.cancelled).toString();
    }
}
