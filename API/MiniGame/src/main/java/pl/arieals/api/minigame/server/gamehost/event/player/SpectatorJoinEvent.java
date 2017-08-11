package pl.arieals.api.minigame.server.gamehost.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arena", this.arena).toString();
    }
}
