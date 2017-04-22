package pl.arieals.api.minigame.server.gamehost.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;

public class GamePhaseChangedEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private final LocalArena arena;
    private final GamePhase  oldPhase;

    public GamePhaseChangedEvent(final LocalArena arena, final GamePhase oldPhase)
    {
        this.arena = arena;
        this.oldPhase = oldPhase;
    }

    public LocalArena getArena()
    {
        return this.arena;
    }

    public GamePhase getNewPhase()
    {
        return this.arena.getGamePhase();
    }

    public GamePhase getOldPhase()
    {
        return this.oldPhase;
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arena", this.arena).append("oldPhase", this.oldPhase).toString();
    }
}
