package pl.north93.zgame.api.bukkit.player.event;

import java.util.Collection;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.global.network.server.joinaction.IServerJoinAction;

/**
 * Event wywolywany po wejsciu gracza gdy zostaly pobrane jego dane
 * i akcje do wykonania po wejsciu na serwer.
 * Jest on normalnie synchroniczny do watku serwera.
 */
public class PlayerDataLoadedEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private final INorthPlayer northPlayer;
    private final Collection<IServerJoinAction> joinActions;

    public PlayerDataLoadedEvent(final INorthPlayer northPlayer, final Collection<IServerJoinAction> joinActions)
    {
        this.northPlayer = northPlayer;
        this.joinActions = joinActions;
    }

    public INorthPlayer getNorthPlayer()
    {
        return this.northPlayer;
    }

    /**
     * Zwraca modyfikowalna liste akcji ktora zostanie wykonana po tym evencie.
     * Zostala pobrana z redisa i z niego skasowana.
     *
     * @return modyfikowalna lista akcji do wykonania.
     */
    public Collection<IServerJoinAction> getJoinActions()
    {
        return this.joinActions;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("northPlayer", this.northPlayer).append("joinActions", this.joinActions).toString();
    }
}
