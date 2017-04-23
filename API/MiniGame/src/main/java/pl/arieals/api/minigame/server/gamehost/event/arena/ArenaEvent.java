package pl.arieals.api.minigame.server.gamehost.event.arena;

import org.bukkit.event.Event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;

public abstract class ArenaEvent extends Event
{
    protected final LocalArena arena;

    public ArenaEvent(final LocalArena arena)
    {
        this.arena = arena;
    }

    public LocalArena getArena()
    {
        return this.arena;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arena", this.arena).toString();
    }
}
