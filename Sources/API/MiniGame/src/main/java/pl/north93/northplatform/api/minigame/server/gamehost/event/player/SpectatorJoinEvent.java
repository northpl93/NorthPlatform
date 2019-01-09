package pl.north93.northplatform.api.minigame.server.gamehost.event.player;

import org.bukkit.event.HandlerList;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

/**
 * Event wywolywany gdy spectator wchodzi do gry.
 */
public class SpectatorJoinEvent extends PlayerArenaEvent
{
    private static final HandlerList handlers = new HandlerList();

    public SpectatorJoinEvent(final INorthPlayer who, final LocalArena arena)
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
