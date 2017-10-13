package pl.arieals.lobby.chest.opening.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.lobby.chest.opening.IOpeningSession;

public class NextChestEvent extends PlayerEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private final IOpeningSession openingSession;
    private       boolean         cancelled;

    public NextChestEvent(final Player who, final IOpeningSession openingSession)
    {
        super(who);
        this.openingSession = openingSession;
    }

    public IOpeningSession getOpeningSession()
    {
        return this.openingSession;
    }

    @Override
    public boolean isCancelled()
    {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean b)
    {
        this.cancelled = b;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("openingSession", this.openingSession).append("cancelled", this.cancelled).toString();
    }
}
