package pl.north93.northplatform.api.bungee.proxy.impl.listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.north93.northplatform.api.global.network.INetworkManager;
import pl.north93.northplatform.api.global.network.players.IOnlinePlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.permissions.Group;

public class PermissionsListener implements Listener
{
    @Inject
    private INetworkManager networkManager;

    @EventHandler
    public void onPermissionCheck(final PermissionCheckEvent event)
    {
        // todo rewrite
        final IOnlinePlayer online = this.networkManager.getPlayers().unsafe().getOnlineValue(event.getSender().getName()).get();
        if (online == null)
        {
            return;
        }
        final Group group = online.getGroup();
        if (group.getPermissions().contains("*")) // TODO implement asterisk handling
        {
            event.setHasPermission(true);
        }
        else
        {
            event.setHasPermission(group.hasPermission(event.getPermission()));
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
