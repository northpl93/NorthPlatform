package pl.north93.zgame.datashare.bungee;

import static java.util.UUID.fromString;


import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.datashare.api.DataSharingGroup;
import pl.north93.zgame.datashare.api.IDataShareController;
import pl.north93.zgame.datashare.sharedimpl.PlayerDataShareComponent;

public class PlayerJoinListener implements Listener
{
    private ApiCore                     apiCore;
    @InjectComponent("PlayerDataShare.Bungee")
    private PlayerDataShareBungee       playerDataShareBungee;
    @InjectComponent("PlayerDataShare.SharedImpl")
    private PlayerDataShareComponent    playerDataShareComponent;
    private Map<UUID, DataSharingGroup> requiresInventorySent = Maps.newHashMap();

    @EventHandler
    public void onServerConnect(final ServerConnectedEvent event)
    {
        final IDataShareController dataController = this.playerDataShareBungee.getController();
        final Server oldBungeeServer = event.getPlayer().getServer();

        final DataSharingGroup newGroup = dataController.getMyGroup(fromString(event.getServer().getInfo().getName()));
        if (newGroup == null)
        {
            return;
        }

        final UUID playerId = event.getPlayer().getUniqueId();

        if (oldBungeeServer == null)
        {
            this.requiresInventorySent.put(playerId, newGroup);
            return;
        }

        final DataSharingGroup oldGroup = dataController.getMyGroup(fromString(oldBungeeServer.getInfo().getName()));

        if (oldGroup == null || ! newGroup.getName().equals(oldGroup.getName()))
        {
            this.requiresInventorySent.put(playerId, newGroup);
        }
    }

    @EventHandler
    public void onServerSwitch(final ServerSwitchEvent event)
    {
        final UUID playerId = event.getPlayer().getUniqueId();
        final DataSharingGroup group = this.requiresInventorySent.remove(playerId);
        if (group == null)
        {
            return;
        }

        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            this.playerDataShareComponent.getDataShareManager().loadPlayer(group, playerId);
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
