package pl.north93.zgame.api.bukkit.player.impl;

import java.text.MessageFormat;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.permissions.PermissionsInjector;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.player.event.PlayerDataLoadedEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.permissions.Group;

public class JoinLeftListener implements Listener
{
    @Inject
    private BukkitApiCore apiCore;

    @EventHandler(priority = EventPriority.LOW)
    public void startAsyncPlayerDataLoading(final PlayerJoinEvent event)
    {
        final PlayerDataLoadTask dataLoadTask = new PlayerDataLoadTask(event.getPlayer());
        this.apiCore.getPlatformConnector().runTaskAsynchronously(dataLoadTask);

        event.setJoinMessage(null);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void handleLoadedData(final PlayerDataLoadedEvent event)
    {
        final INorthPlayer player = event.getNorthPlayer();
        final Group group = player.getGroup();

        PermissionsInjector.inject(player.getCraftPlayer());
        final PermissionAttachment attachment = player.addAttachment(this.apiCore.getPluginMain());
        this.addPermissions(attachment, player.getGroup());

        if (! StringUtils.isEmpty(player.getGroup().getJoinMessage())) // send message
        {
            Bukkit.broadcastMessage(MessageFormat.format(group.getJoinMessage(), player.getName()));
        }
    }

    @EventHandler
    public void onLeave(final PlayerQuitEvent event)
    {
        event.setQuitMessage(null);
    }

    private void addPermissions(final PermissionAttachment attachment, final Group group)
    {
        for (final String permission : group.getPermissions())
        {
            if (permission.startsWith("-"))
            {
                attachment.setPermission(permission.substring(1, permission.length()), false);
            }
            else
            {
                attachment.setPermission(permission, true);
            }
        }
        for (final Group inheritGroup : group.getInheritance())
        {
            this.addPermissions(attachment, inheritGroup);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
