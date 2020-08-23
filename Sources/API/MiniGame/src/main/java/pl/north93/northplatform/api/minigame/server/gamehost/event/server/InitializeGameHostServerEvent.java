package pl.north93.northplatform.api.minigame.server.gamehost.event.server;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class InitializeGameHostServerEvent extends Event
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
