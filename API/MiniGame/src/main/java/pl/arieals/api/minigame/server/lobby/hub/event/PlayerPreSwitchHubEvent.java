package pl.arieals.api.minigame.server.lobby.hub.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.lobby.hub.HubWorld;

public class PlayerPreSwitchHubEvent extends PlayerHubEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public PlayerPreSwitchHubEvent(final Player who, final HubWorld oldHub, final HubWorld newHub)
    {
        super(who, oldHub, newHub);
    }

    @Override
    public boolean isCancelled()
    {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean cancelled)
    {
        this.cancelled = cancelled;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("cancelled", this.cancelled).toString();
    }
}
