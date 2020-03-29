package pl.north93.northplatform.api.minigame.server.gamehost.event.arena;

import org.bukkit.event.HandlerList;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;

public class ArenaStartCancelledEvent extends ArenaEvent
{
    private static final HandlerList handlers = new HandlerList();
    
    public ArenaStartCancelledEvent(final LocalArena arena)
    {
        super(arena);
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
}
