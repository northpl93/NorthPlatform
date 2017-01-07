package pl.north93.zgame.datashare.bungee;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.datashare.api.DataSharingGroup;
import pl.north93.zgame.datashare.sharedimpl.PlayerDataShareComponent;

public class PlayerJoinListener implements Listener
{
    private ApiCore                  apiCore;
    @InjectComponent("PlayerDataShare.Bungee")
    private PlayerDataShareBungee    playerDataShareBungee;
    @InjectComponent("PlayerDataShare.SharedImpl")
    private PlayerDataShareComponent playerDataShareComponent;

    @EventHandler
    public void onServerConnect(final ServerConnectedEvent event)
    {
        final Server oldBungeeServer = event.getPlayer().getServer();
        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            final DataSharingGroup newGroup = this.playerDataShareBungee.getController().getMyGroup(UUID.fromString(event.getServer().getInfo().getName()));

            if (newGroup == null)
            {
                return;
            }

            final UUID playerId = event.getPlayer().getUniqueId();

            if (oldBungeeServer == null)
            {
                this.playerDataShareComponent.getDataShareManager().loadPlayer(newGroup, playerId);
                return;
            }

            final DataSharingGroup oldGroup = this.playerDataShareBungee.getController().getMyGroup(UUID.fromString(oldBungeeServer.getInfo().getName()));

            if (oldGroup == null || ! newGroup.getName().equals(oldGroup.getName()))
            {
                this.playerDataShareComponent.getDataShareManager().loadPlayer(newGroup, playerId);
            }
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
