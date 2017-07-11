package pl.arieals.api.minigame.server.gamehost.event.arena.deathmatch;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.ArenaEvent;
import pl.arieals.api.minigame.shared.api.arena.DeathMatchState;

/**
 * Event wywoływany przed rozpoczęciem uruchamiania deathmatchu. <br>
 * Podczas wykonywania stan deathmatchu to {@link DeathMatchState#NOT_STARTED}.
 */
public class DeathMatchPrepareEvent extends ArenaEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public DeathMatchPrepareEvent(final LocalArena arena)
    {
        super(arena);
    }

    @Override
    public boolean isCancelled()
    {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean cancelled)
    {
        this.cancelled = cancelled;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("cancelled", this.cancelled).toString();
    }
}
