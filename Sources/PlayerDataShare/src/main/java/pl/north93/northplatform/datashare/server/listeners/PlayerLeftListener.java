package pl.north93.northplatform.datashare.server.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.datashare.server.PlayerDataShareServer;
import pl.north93.northplatform.datashare.sharedimpl.PlayerDataShareComponent;

public class PlayerLeftListener implements Listener
{
    @Inject
    private ApiCore                  apiCore;
    @Inject
    private PlayerDataShareComponent dataShareManager;
    @Inject
    private PlayerDataShareServer    dataShareServer;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeft(final PlayerQuitEvent event)
    {
        this.apiCore.getHostConnector().runTaskAsynchronously(() ->
        {
            this.dataShareManager.getDataShareManager().savePlayer(this.dataShareServer.getMyGroup(), event.getPlayer(), true);
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
