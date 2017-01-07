package pl.north93.zgame.datashare.server.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.datashare.server.PlayerDataShareServer;
import pl.north93.zgame.datashare.sharedimpl.PlayerDataShareComponent;

public class PlayerLeftListener implements Listener
{
    private ApiCore                  apiCore;
    @InjectComponent("PlayerDataShare.SharedImpl")
    private PlayerDataShareComponent dataShareManager;
    @InjectComponent("PlayerDataShare.Bukkit")
    private PlayerDataShareServer    dataShareServer;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeft(final PlayerQuitEvent event)
    {
        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            this.dataShareManager.getDataShareManager().savePlayer(this.dataShareServer.getMyGroup(), event.getPlayer());
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
