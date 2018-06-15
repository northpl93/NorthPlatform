package pl.arieals.api.minigame.server.gamehost.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;

/**
 * Event wywolywany gdy spectator wchodzi do gry.
 */
public class SpectatorJoinEvent extends PlayerArenaEvent
{
    private static final HandlerList handlers = new HandlerList();

    public SpectatorJoinEvent(final Player who, final LocalArena arena)
    {
        super(arena, who);
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
