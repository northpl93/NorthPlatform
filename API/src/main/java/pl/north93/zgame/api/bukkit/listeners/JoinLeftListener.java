package pl.north93.zgame.api.bukkit.listeners;

import static org.bukkit.ChatColor.RED;


import java.text.MessageFormat;

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
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.impl.Injector;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.server.joinaction.IServerJoinAction;
import pl.north93.zgame.api.global.network.server.joinaction.JoinActionsContainer;
import pl.north93.zgame.api.global.permissions.Group;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;

public class JoinLeftListener implements Listener
{
    private BukkitApiCore       bukkitApiCore;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager     networkManager;
    @InjectComponent("API.Database.Redis.Observer")
    private IObservationManager observation;

    public JoinLeftListener()
    {
        Injector.inject(API.getApiCore().getComponentManager(), this);
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        final IOnlinePlayer iplayer = this.networkManager.getOnlinePlayer(player.getName()).get();
        if (iplayer == null)
        {
            player.kickPlayer(RED + "Połącz się z serwerem ponownie (iplayer==null in onJoin)");
            return;
        }
        final Group group = iplayer.getGroup();

        event.setJoinMessage(null);

        PermissionsInjector.inject(player);
        final PermissionAttachment attachment = player.addAttachment(this.bukkitApiCore.getPluginMain());
        this.addPermissions(attachment, group);

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
        // TODO
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
            final JoinActionsContainer joinActionsContainer = actions.get();
            if (joinActionsContainer == null)
            {
                return null;
            }
            actions.delete();
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
