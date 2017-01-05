package pl.north93.zgame.api.bukkit.listeners;

import java.text.MessageFormat;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

import org.apache.commons.lang3.StringUtils;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.network.IOnlinePlayer;
import pl.north93.zgame.api.global.permissions.Group;
import pl.north93.zgame.api.global.redis.observable.Value;

public class JoinLeftListener implements Listener
{
    @EventHandler
    public void onJoin(final PlayerJoinEvent event)
    {
        event.setJoinMessage(null);
        final Value<IOnlinePlayer> networkPlayer = API.getApiCore().getNetworkManager().getOnlinePlayer(event.getPlayer().getName());
        final Group group = networkPlayer.get().getGroup();

        final PermissionAttachment attachment = event.getPlayer().addAttachment(((BukkitApiCore) API.getApiCore()).getPluginMain());
        this.addPermissions(attachment, group);

        if (! StringUtils.isEmpty(group.getJoinMessage())) // send message
        {
            Bukkit.broadcastMessage(MessageFormat.format(group.getJoinMessage(), event.getPlayer().getName()));
        }
    }

    @EventHandler
    public void onLeave(final PlayerQuitEvent event)
    {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onKick(final PlayerKickEvent event)
    {
        // TODO
    }

    private void addPermissions(final PermissionAttachment attachment, final Group group)
    {
        for (final String permission : group.getPermissions())
        {
            attachment.setPermission(permission, true);
        }
        for (final Group inheritGroup : group.getInheritance())
        {
            this.addPermissions(attachment, inheritGroup);
        }
    }
}
