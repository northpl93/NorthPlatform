package pl.north93.zgame.api.bungee.listeners;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.north93.zgame.api.bungee.BungeeApiCore;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.permissions.Group;

public class PermissionsListener implements Listener
{
    private BungeeApiCore apiCore;

    public PermissionsListener(final BungeeApiCore apiCore)
    {
        this.apiCore = apiCore;
    }

    @EventHandler
    public void onPermissionCheck(final PermissionCheckEvent event)
    {
        // todo rewrite
        final IOnlinePlayer online = this.apiCore.getNetworkManager().getPlayers().unsafe().getOnline(event.getSender().getName()).get();
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
