package pl.north93.northplatform.api.minigame.server.gamehost.event.arena.gamephase;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.ArenaEvent;

/**
 * Event wywołujący się jako pierwszy po utworzeniu areny oraz
 * przy każdym nowym cyklu.
 * <p>
 * Ten event może zostać anulowany jesli arena zostaje przeznaczona
 * do usuniecia, np. gdy serwer zostal zaplanowany do wylaczenia.
 * <p>
 * Zalecane jest uzywanie {@code ignoreCancelled=true}
 */
public class GameInitEvent extends ArenaEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public GameInitEvent(final LocalArena arena)
    {
        super(arena);
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    @Override
    public boolean isCancelled()
    {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean b)
    {
        this.cancelled = b;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("cancelled", this.cancelled).toString();
    }
}
