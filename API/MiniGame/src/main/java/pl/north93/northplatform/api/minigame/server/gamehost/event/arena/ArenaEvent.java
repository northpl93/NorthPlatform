package pl.north93.northplatform.api.minigame.server.gamehost.event.arena;

import org.bukkit.event.Event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;

/**
 * Reprezentuje event dotyczący danej areny minigry.
 */
public abstract class ArenaEvent extends Event
{
    protected final LocalArena arena;

    public ArenaEvent(final LocalArena arena)
    {
        this.arena = arena;
    }

    /**
     * Zwraca arene której dotyczy dany event.
     * @return arena powiązana z eventem.
     */
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
