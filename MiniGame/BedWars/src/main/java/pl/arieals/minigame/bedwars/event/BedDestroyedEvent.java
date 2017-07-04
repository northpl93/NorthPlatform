package pl.arieals.minigame.bedwars.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.ArenaEvent;
import pl.arieals.minigame.bedwars.arena.Team;

/**
 * Event wywoływany gdy łózko danego teamu zostanie zniszczone.
 */
public class BedDestroyedEvent extends ArenaEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final Player destroyer;
    private final Block  bedBlock;
    private final Team   team;

    public BedDestroyedEvent(final LocalArena arena, final Player destroyer, final Block bedBlock, final Team team)
    {
        super(arena);
        this.destroyer = destroyer;
        this.bedBlock = bedBlock;
        this.team = team;
    }

    /**
     * Zwraca gracza który zniszczył łóżko.
     *
     * @return gracz który zniszczył łóżko.
     */
    public Player getDestroyer()
    {
        return this.destroyer;
    }

    /**
     * Zwraca zniszczony blok łóżka.
     *
     * @return blok łóżka.
     */
    public Block getBedBlock()
    {
        return this.bedBlock;
    }

    /**
     * Zwraca team do którego należało łóżko.
     *
     * @return team do którego należało łóżko.
     */
    public Team getTeam()
    {
        return this.team;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("destroyer", this.destroyer).append("bedBlock", this.bedBlock).append("team", this.team).toString();
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
