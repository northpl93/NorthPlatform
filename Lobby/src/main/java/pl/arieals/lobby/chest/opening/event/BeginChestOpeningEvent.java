package pl.arieals.lobby.chest.opening.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.lobby.chest.ChestType;
import pl.arieals.lobby.chest.opening.IOpeningSession;

public class BeginChestOpeningEvent extends PlayerEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private final IOpeningSession session;
    private final ChestType       chestType;
    private boolean isCancelled;

    public BeginChestOpeningEvent(final Player who, final IOpeningSession session, final ChestType chestType)
    {
        super(who);
        this.session = session;
        this.chestType = chestType;
    }

    public IOpeningSession getSession()
    {
        return this.session;
    }

    public ChestType getChestType()
    {
        return this.chestType;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    @Override
    public boolean isCancelled()
    {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(final boolean b)
    {
        this.isCancelled = b;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("session", this.session).append("chestType", this.chestType).append("isCancelled", this.isCancelled).toString();
    }
}
