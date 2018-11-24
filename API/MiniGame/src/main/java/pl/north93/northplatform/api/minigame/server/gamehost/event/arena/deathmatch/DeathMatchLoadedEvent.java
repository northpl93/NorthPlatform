package pl.north93.northplatform.api.minigame.server.gamehost.event.arena.deathmatch;

import org.bukkit.World;
import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.ArenaEvent;

/**
 * Event wywołuje się gdy arena do deathmatchu jest już załadowana i
 * jest ustawiona jako główna mapa areny. <br>
 *
 * {@link LocalArena#getWorld()} w tym evencie zwraca już informacje
 * o nowej mapie do death matchu. <br>
 *
 * Plugin powinien anulować wszystkie swoje zadania związane z
 * starą mapą, aby mogła być bezpiecznie usunięta. <br>
 *
 * Natychmiastowo po tym evencie stara mapa zostanie usunięta.
 */
public class DeathMatchLoadedEvent extends ArenaEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final World oldWorld;
    private final World newWorld;

    public DeathMatchLoadedEvent(final LocalArena arena, final World oldWorld, final World newWorld)
    {
        super(arena);
        this.oldWorld = oldWorld;
        this.newWorld = newWorld;
    }

    /**
     * @return domyślny świat areny, sprzed zmiany.
     */
    public World getOldWorld()
    {
        return this.oldWorld;
    }

    /**
     * @return nowy świat areny, z areną do deathmatchu.
     */
    public World getNewWorld()
    {
        return this.newWorld;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("oldWorld", this.oldWorld).append("newWorld", this.newWorld).toString();
    }
}
