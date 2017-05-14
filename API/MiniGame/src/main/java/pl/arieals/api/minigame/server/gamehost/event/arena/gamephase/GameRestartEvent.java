package pl.arieals.api.minigame.server.gamehost.event.arena.gamephase;

import org.bukkit.event.HandlerList;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.ArenaEvent;

public class GameRestartEvent extends ArenaEvent
{
    private static final HandlerList handlers = new HandlerList();

    public GameRestartEvent(final LocalArena arena)
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
