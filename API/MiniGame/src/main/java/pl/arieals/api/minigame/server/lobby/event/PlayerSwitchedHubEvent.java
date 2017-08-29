package pl.arieals.api.minigame.server.lobby.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.lobby.hub.HubWorld;

/**
 * Event wywolywany gdy gracz wchodzi na nowego huba na tym serwerze.
 * Wywolywany jest takze gdy gracz pierwszy raz wchodzi na serwer
 * lub gdy zostal przeniesiony z innego serwera.
 */
public class PlayerSwitchedHubEvent extends PlayerEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final HubWorld newHub;

    public PlayerSwitchedHubEvent(final Player who, final HubWorld newHub)
    {
        super(who);
        this.newHub = newHub;
    }

    public HubWorld getNewHub()
    {
        return this.newHub;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("newHub", this.newHub).toString();
    }
}
