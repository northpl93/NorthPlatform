package pl.arieals.api.minigame.server.gamehost.event.arena;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;

public class ArenaStartScheduledEvent extends ArenaEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    
    private boolean cancelled;
    private int startDelay;
    
    public ArenaStartScheduledEvent(LocalArena arena, int startDelay)
    {
        super(arena);
        this.startDelay = startDelay;
    }
    
    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }
    
    public int getStartDelay()
    {
        return startDelay;
    }
    
    public void setStartDelay(int startDelay)
    {
        this.startDelay = startDelay;
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
