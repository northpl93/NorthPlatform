package pl.arieals.api.minigame.server.lobby.hub.listener;

import org.bukkit.event.EventHandler;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.lobby.hub.event.PlayerSwitchedHubEvent;
import pl.arieals.api.minigame.server.lobby.hub.visibility.HubVisibilityService;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class PlayerVisibilityListener implements AutoListener
{
    @Inject
    private HubVisibilityService hubVisibilityService;

    @EventHandler
    public void updateVisibilityOnHubSwitch(final PlayerSwitchedHubEvent event)
    {
        final INorthPlayer player = INorthPlayer.wrap(event.getPlayer());
        this.hubVisibilityService.refreshVisibility(player);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
