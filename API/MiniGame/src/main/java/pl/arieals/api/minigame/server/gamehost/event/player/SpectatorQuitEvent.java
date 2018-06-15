package pl.arieals.api.minigame.server.gamehost.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;

/**
 * Event wywoływany gdy spectator wychodzi z gry.
 */
public class SpectatorQuitEvent extends PlayerArenaEvent
{
    private static final HandlerList handlers = new HandlerList();

    public SpectatorQuitEvent(final LocalArena arena, final Player player)
    {
        super(arena, player);
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
