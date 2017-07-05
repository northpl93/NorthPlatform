package pl.arieals.api.minigame.server.gamehost.event.arena;

import org.bukkit.World;
import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;

/**
 * Event wywołuje się gdy arena do deathmatchu jest już załadowana i
 * jest ustawiona jako główna mapa areny.
 *
 * {@code LocalArena#getWorld()} w tym evencie zwraca już informacje
 * o nowej mapie do death matchu.
 *
 * Plugin powinien przeteleportować graczy na arenę i anulować
 * wszystkie swoje zadania związane z starą mapą, aby mogła
 * być bezpiecznie usunięta.
 *
 * Natychmiastowo po tym evencie stara mapa zostanie usunięta.
 */
public class DeathMatchPrepareEvent extends ArenaEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final World oldWorld;
    private final World newWorld;

    public DeathMatchPrepareEvent(final LocalArena arena, final World oldWorld, final World newWorld)
    {
        super(arena);
        this.oldWorld = oldWorld;
        this.newWorld = newWorld;
    }

    public World getOldWorld()
    {
        return this.oldWorld;
    }

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
