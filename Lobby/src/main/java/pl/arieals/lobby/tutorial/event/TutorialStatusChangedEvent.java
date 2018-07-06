package pl.arieals.lobby.tutorial.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.lobby.tutorial.TutorialStatus;
import pl.north93.zgame.api.global.network.players.Identity;

public class TutorialStatusChangedEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private final Identity       identity;
    private final String         tutorialId;
    private final TutorialStatus newStatus;

    public TutorialStatusChangedEvent(final Identity identity, final String tutorialId, final TutorialStatus newStatus)
    {
        this.identity = identity;
        this.tutorialId = tutorialId;
        this.newStatus = newStatus;
    }

    public Identity getIdentity()
    {
        return this.identity;
    }

    public String getTutorialId()
    {
        return this.tutorialId;
    }

    public TutorialStatus getNewStatus()
    {
        return this.newStatus;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("identity", this.identity).append("tutorialId", this.tutorialId).append("newStatus", this.newStatus).toString();
    }
}
