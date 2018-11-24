package pl.north93.northplatform.api.minigame.server.gamehost.event.arena.deathmatch;

import org.bukkit.event.HandlerList;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.ArenaEvent;

/**
 * Event wywoływany gdy rozpocznie się okres przygotowania
 * na arenie do deathmatchu.
 * <p>
 * Po tym evencie zostanie odblokowane PvP na arenie.
 */
public class DeathMatchFightStartEvent extends ArenaEvent
{
    private static final HandlerList handlers = new HandlerList();

    public DeathMatchFightStartEvent(final LocalArena arena)
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
