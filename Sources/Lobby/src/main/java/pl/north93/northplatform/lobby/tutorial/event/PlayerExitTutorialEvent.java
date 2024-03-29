package pl.north93.northplatform.lobby.tutorial.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import pl.north93.northplatform.api.minigame.server.lobby.hub.HubWorld;

public class PlayerExitTutorialEvent extends PlayerEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final HubWorld tutorialHub;

    public PlayerExitTutorialEvent(final Player who, final HubWorld tutorialHub)
    {
        super(who);
        this.tutorialHub = tutorialHub;
    }

    public HubWorld getTutorialHub()
    {
        return this.tutorialHub;
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
