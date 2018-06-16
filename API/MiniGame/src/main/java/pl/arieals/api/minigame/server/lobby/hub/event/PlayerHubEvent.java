package pl.arieals.api.minigame.server.lobby.hub.event;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.lobby.hub.HubWorld;

public abstract class PlayerHubEvent extends PlayerEvent
{
    private final HubWorld oldHub;
    private final HubWorld newHub;

    public PlayerHubEvent(final Player who, final HubWorld oldHub, final HubWorld newHub)
    {
        super(who);
        this.oldHub = oldHub;
        this.newHub = newHub;
    }

    @Nullable
    public HubWorld getOldHub()
    {
        return this.oldHub;
    }

    public HubWorld getNewHub()
    {
        return this.newHub;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("oldHub", this.oldHub).append("newHub", this.newHub).toString();
    }
}
