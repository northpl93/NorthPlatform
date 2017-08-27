package pl.north93.zgame.api.bukkit.player.impl;

import static org.bukkit.ChatColor.RED;

import static pl.north93.zgame.api.bukkit.player.impl.LanguageKeeper.updateLocale;


import java.text.MessageFormat;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.permissions.PermissionsInjector;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.server.joinaction.IServerJoinAction;
import pl.north93.zgame.api.global.network.server.joinaction.JoinActionsContainer;
import pl.north93.zgame.api.global.permissions.Group;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;

public class JoinLeftListener implements Listener
{
    @Inject
    private BukkitApiCore       bukkitApiCore;
    @Inject
    private INetworkManager     networkManager;
    @Inject
    private IObservationManager observation;

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) // todo rewrite
    {
        event.setJoinMessage(null);

        final Player player = event.getPlayer();
        final IOnlinePlayer iplayer = this.networkManager.getPlayers().unsafe().getOnline(player.getName()).get();
        if (iplayer == null)
        {
            this.bukkitApiCore.getLogger().log(Level.SEVERE, "Player {0} ({1}) joined, but iplayer is null in onJoin", new Object[]{player.getName(), player.getUniqueId()});
            player.kickPlayer(RED + "Połącz się z serwerem ponownie (iplayer==null in JoinLeftListener#onJoin)");
            return;
        }

        final Group group = iplayer.getGroup();
        if (group == null)
        {
            this.bukkitApiCore.getLogger().log(Level.SEVERE, "Group is null in JoinLeftListener. player:{0}", player.getName());
            player.kickPlayer(RED + "Połącz się z serwerem ponownie (group==null in JoinLeftListener#onJoin)");
            return;
        }

        updateLocale(player, iplayer.getLocale());

        PermissionsInjector.inject(player);
        final PermissionAttachment attachment = player.addAttachment(this.bukkitApiCore.getPluginMain());
        this.addPermissions(attachment, group);

        if (iplayer.hasDisplayName())
        {
            player.setDisplayName(iplayer.getDisplayName());
        }

        this.doOnJoinActions(player);

        if (! StringUtils.isEmpty(group.getJoinMessage())) // send message
        {
            Bukkit.broadcastMessage(MessageFormat.format(group.getJoinMessage(), player.getName()));
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
        event.setLeaveMessage(null);
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

    private void doOnJoinActions(final Player player)
    {
        this.bukkitApiCore.sync(() ->
        {
            final Value<JoinActionsContainer> actions = this.observation.get(JoinActionsContainer.class, "serveractions:" + player.getName());
            final JoinActionsContainer joinActionsContainer = actions.getAndDelete();
            if (joinActionsContainer == null)
            {
                return null;
            }
            return joinActionsContainer.getServerJoinActions();
        }, (actions) ->
        {
            for (final IServerJoinAction iServerJoinAction : actions)
            {
                iServerJoinAction.playerJoined(player);
            }
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
