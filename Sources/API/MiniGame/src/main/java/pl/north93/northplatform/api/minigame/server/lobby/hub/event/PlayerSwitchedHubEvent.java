package pl.north93.northplatform.api.minigame.server.lobby.hub.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import pl.north93.northplatform.api.minigame.server.lobby.hub.HubWorld;

/**
 * Event wywolywany gdy gracz wchodzi na nowego huba na tym serwerze.
 * Wywolywany jest takze gdy gracz pierwszy raz wchodzi na serwer
 * lub gdy zostal przeniesiony z innego serwera.
 */
public class PlayerSwitchedHubEvent extends PlayerHubEvent
{
    private static final HandlerList handlers = new HandlerList();

    public PlayerSwitchedHubEvent(final Player who, final HubWorld oldHub, final HubWorld newHub)
    {
        super(who, oldHub, newHub);
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
