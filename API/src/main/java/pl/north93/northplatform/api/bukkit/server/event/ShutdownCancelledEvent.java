package pl.north93.northplatform.api.bukkit.server.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event wykonuje sie gdy wylaczenie serwera zostalo anulowane.
 */
public class ShutdownCancelledEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
