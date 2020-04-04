package pl.north93.northplatform.api.bukkit.player.event;

import java.util.Collection;

import org.bukkit.event.HandlerList;

import lombok.ToString;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.network.server.joinaction.IServerJoinAction;

/**
 * Event wywolywany po wejsciu gracza gdy zostaly pobrane jego dane
 * i akcje do wykonania po wejsciu na serwer.
 * Jest on normalnie synchroniczny do watku serwera.
 */
@ToString(of = {"joinActions"})
public class PlayerDataLoadedEvent extends NorthPlayerEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final Collection<IServerJoinAction> joinActions;

    public PlayerDataLoadedEvent(final INorthPlayer northPlayer, final Collection<IServerJoinAction> joinActions)
    {
        super(northPlayer);
        this.joinActions = joinActions;
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
}
