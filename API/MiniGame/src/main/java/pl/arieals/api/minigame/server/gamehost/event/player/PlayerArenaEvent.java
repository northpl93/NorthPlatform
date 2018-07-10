package pl.arieals.api.minigame.server.gamehost.event.player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.ArenaEvent;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;

/**
 * Event dotyczacy gracza powiazanego z dana arena.
 */
public abstract class PlayerArenaEvent extends ArenaEvent
{
    private final INorthPlayer player;

    public PlayerArenaEvent(final LocalArena arena, final INorthPlayer player)
    {
        super(arena);
        this.player = player;
    }

    /**
     * Zwraca gracza ktorego dotyczy ten event.
     * @return gracz ktorego dotyczy event.
     */
    public INorthPlayer getPlayer()
    {
        return this.player;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).toString();
    }
}
