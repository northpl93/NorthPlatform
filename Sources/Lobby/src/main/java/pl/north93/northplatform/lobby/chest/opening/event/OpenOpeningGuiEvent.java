package pl.north93.northplatform.lobby.chest.opening.event;

import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.event.NorthPlayerEvent;
import pl.north93.northplatform.api.minigame.server.lobby.hub.HubWorld;
import pl.north93.northplatform.lobby.chest.opening.HubOpeningConfig;
import pl.north93.northplatform.lobby.chest.opening.IOpeningSession;

/**
 * Event ktory wykonuje sie gdy gracz wchodzi do gui otwierania skrzynki
 * na konkretnym hubie.
 */
public class OpenOpeningGuiEvent extends NorthPlayerEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final IOpeningSession session;

    public OpenOpeningGuiEvent(final IOpeningSession session)
    {
        super(session.getPlayer());
        this.session = session;
    }

    public IOpeningSession getSession()
    {
        return this.session;
    }

    public HubWorld getHub()
    {
        return this.session.getHub();
    }

    public HubOpeningConfig getConfig()
    {
        return this.session.getConfig();
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("session", this.session).toString();
    }
}
