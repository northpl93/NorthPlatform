package pl.arieals.api.minigame.server.gamehost.event.arena;

import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.MapTemplate;

public class MapSwitchedEvent extends ArenaEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final MapSwitchReason reason;

    public MapSwitchedEvent(final LocalArena arena, final MapSwitchReason reason)
    {
        super(arena);
        this.reason = reason;
    }

    public MapTemplate getGameMap()
    {
        return this.arena.getWorld().getCurrentMapTemplate();
    }

    public MapSwitchReason getReason()
    {
        return this.reason;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("reason", this.reason).toString();
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    public enum MapSwitchReason
    {
        ARENA_INITIALISE,
        DEATH_MATCH
    }
}
