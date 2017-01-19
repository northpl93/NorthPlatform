package pl.north93.zgame.api.bungee.listeners;

import org.bukkit.event.EventHandler;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import pl.north93.zgame.api.bungee.BungeeApiCore;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;

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
        final Value<IOnlinePlayer> value = this.apiCore.getNetworkManager().getOnlinePlayer(event.getSender().getName());
        final IOnlinePlayer online = value.get();
        if (online == null)
        {
            return;
        }
        event.setHasPermission(online.getGroup().hasPermission(event.getPermission()));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
