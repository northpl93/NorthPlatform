package pl.north93.northplatform.api.minigame.server.gamehost.event.player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.shared.api.PlayerStatus;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

/**
 * Event wywoływany gdy zmieniany jest tryb spectatora dla danego gracza.
 */
public class SpectatorModeChangeEvent extends PlayerArenaEvent
{
    private static final HandlerList  handlers = new HandlerList();
    private final        PlayerStatus oldStatus;
    private final        PlayerStatus newStatus;

    public SpectatorModeChangeEvent(final LocalArena arena, final INorthPlayer who, final PlayerStatus oldStatus, final PlayerStatus newStatus)
    {
        super(arena, who);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    /**
     * Zwraca poprzedni status gracza.
     * Moze byc nullem jak gracz wchodzi na serwer.
     * @return poprzedni status gracza.
     */
    public @Nullable PlayerStatus getOldStatus()
    {
        return this.oldStatus;
    }

    public @Nonnull PlayerStatus getNewStatus()
    {
        return this.newStatus;
    }

    /**
     * Sprawdza czy dany gracz uczestniczy czy w danej grze.
     * @return czy gracz uczestniczy w grze.
     */
    public boolean isParticipating()
    {
        return this.newStatus.isPlaying();
    }

    @Override
    public @Nonnull HandlerList getHandlers()
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("oldStatus", this.oldStatus).append("newStatus", this.newStatus).toString();
    }
}
