package pl.arieals.lobby.chest.opening;

import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import pl.arieals.api.minigame.server.lobby.hub.HubWorld;

public class StartChestOpeningEvent extends PlayerEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final IOpeningSession session;

    public StartChestOpeningEvent(final IOpeningSession session)
    {
        super(session.getPlayer());
        this.session = session;
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
}
