package pl.arieals.api.minigame.server.gamehost.event.player;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.ArenaEvent;

/**
 * Event dotyczacy gracza powiazanego z dana arena.
 */
public abstract class PlayerArenaEvent extends ArenaEvent
{
    private final Player player;

    public PlayerArenaEvent(final LocalArena arena, final Player player)
    {
        super(arena);
        this.player = player;
    }

    /**
     * Zwraca gracza ktorego dotyczy ten event.
     * @return gracz ktorego dotyczy event.
     */
    public Player getPlayer()
    {
        return this.player;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).toString();
    }
}
