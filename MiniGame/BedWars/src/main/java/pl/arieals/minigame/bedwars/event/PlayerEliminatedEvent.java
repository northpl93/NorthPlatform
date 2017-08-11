package pl.arieals.minigame.bedwars.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerArenaEvent;

/**
 * Event wywolywany gdy gracz zostaje wyelininowany, tzn przegrywa gre.
 * Moze zostac wywolany takze gdy gracz jest offline (timeout gdy ma lozko)
 */
public class PlayerEliminatedEvent extends PlayerArenaEvent
{
    private static final HandlerList handlers = new HandlerList();

    public PlayerEliminatedEvent(final LocalArena arena, final Player player)
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
