package pl.arieals.api.minigame.server.gamehost.event.player;

import org.bukkit.event.HandlerList;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;

/**
 * Event wywoływany gdy spectator wychodzi z gry.
 */
public class SpectatorQuitEvent extends PlayerArenaEvent
{
    private static final HandlerList handlers = new HandlerList();

    public SpectatorQuitEvent(final LocalArena arena, final INorthPlayer player)
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
