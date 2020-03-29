package pl.north93.northplatform.minigame.bedwars.event;

import javax.annotation.Nullable;

import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.ArenaEvent;
import pl.north93.northplatform.minigame.bedwars.arena.Team;

/**
 * Event wywoływany gdy łózko danego teamu zostanie zniszczone.
 */
public class BedDestroyedEvent extends ArenaEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final INorthPlayer destroyer;
    private final Block bedBlock;
    private final Team team;
    private final boolean silent;

    public BedDestroyedEvent(final LocalArena arena, final INorthPlayer destroyer, final Block bedBlock, final Team team, final boolean silent)
    {
        super(arena);
        this.destroyer = destroyer;
        this.bedBlock = bedBlock;
        this.team = team;
        this.silent = silent;
    }

    /**
     * Zwraca gracza który zniszczył łóżko.
     * W przypadku zniszczenia lozka przez automat, tu bedzie null.
     *
     * @return gracz który zniszczył łóżko.
     */
    public @Nullable INorthPlayer getDestroyer()
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

    /**
     * Czy zniszczenie powinno pozostac ciche, nie wywolac komunikatu graczom.
     *
     * @return True jesli nie powinny pojawic sie zadne komunikaty dot. zniszczenia lozka.
     */
    public boolean isSilent()
    {
        return this.silent;
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
