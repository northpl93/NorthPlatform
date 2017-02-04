package pl.north93.zgame.api.bungee.listeners;

import static pl.north93.zgame.api.global.API.message;


import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import pl.north93.zgame.api.bungee.BungeeApiCore;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.component.impl.Injector;
import pl.north93.zgame.api.global.data.UsernameCache.UsernameDetails;
import pl.north93.zgame.api.global.data.players.IPlayersData;
import pl.north93.zgame.api.global.data.players.impl.NameSizeMistakeException;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.impl.OnlinePlayerImpl;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;

public class PlayerListener implements Listener
{
    private final BungeeApiCore   bungeeApiCore;
    @InjectResource(bundleName = "Messages")
    private       ResourceBundle  apiMessages;
    @InjectComponent("API.MinecraftNetwork.PlayersStorage")
    private       IPlayersData    playersDao;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private       INetworkManager networkManager;

    public PlayerListener(final BungeeApiCore bungeeApiCore)
    {
        this.bungeeApiCore = bungeeApiCore;
        Injector.inject(bungeeApiCore.getComponentManager(), this); // manually perform injections
    }

    @EventHandler
    public void onLogin(final PreLoginEvent event)
    {
        event.registerIntent(this.bungeeApiCore.getBungeePlugin()); // lock this event

        final PendingConnection connection = event.getConnection();
        this.bungeeApiCore.getPlatformConnector().runTaskAsynchronously(() -> // run async task
        {
            try
            {
                final Optional<UsernameDetails> details = this.bungeeApiCore.getUsernameCache().getUsernameDetails(connection.getName());

                if (! details.isPresent())
                {
                    event.setCancelled(true);
                    event.setCancelReason(ChatColor.RED + this.apiMessages.getString("join.premium.check_failed"));
                    return;
                }

                final UsernameDetails usernameDetails = details.get();
                if (usernameDetails.isPremium() && !usernameDetails.getValidSpelling().equals(connection.getName()))
                {
                    event.setCancelled(true);
                    event.setCancelReason(ChatColor.RED + this.apiMessages.getString("join.premium.name_size_mistake"));
                    return;
                }

                if (this.bungeeApiCore.getNetworkManager().isOnline(connection.getName())) // sprawdzanie czy taki gracz juz jest w sieci
                {
                    event.setCancelled(true);
                    event.setCancelReason(ChatColor.RED + this.apiMessages.getString("join.already_online"));
                    return;
                }

                connection.setOnlineMode(usernameDetails.isPremium());
            }
            finally
            {
                event.completeIntent(this.bungeeApiCore.getBungeePlugin()); // unlock event
            }
        });
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(final LoginEvent event)
    {
        event.registerIntent(this.bungeeApiCore.getBungeePlugin()); // lock this event

        final PendingConnection connection = event.getConnection();
        this.bungeeApiCore.getPlatformConnector().runTaskAsynchronously(() -> // run async task
        {
            try
            {
                final JoiningPolicy joiningPolicy = this.bungeeApiCore.getNetworkManager().getJoiningPolicy();
                final Optional<UsernameDetails> details = this.bungeeApiCore.getUsernameCache().getUsernameDetails(connection.getName());

                if (! details.isPresent())
                {
                    event.setCancelled(true);
                    event.setCancelReason(this.apiMessages.getString("join.username_details_not_present"));
                    return;
                }

                final Value<OnlinePlayerImpl> player;
                try
                {
                    player = this.playersDao.loadPlayer(connection.getUniqueId(), connection.getName(), details.get().isPremium(), this.bungeeApiCore.getProxyConfig().getUniqueName());
                    player.expire(2);
                }
                catch (final NameSizeMistakeException e)
                {
                    event.setCancelled(true);
                    event.setCancelReason(message(this.apiMessages, "join.name_size_mistake", e.getNick(), connection.getName()));
                    return;
                }
                catch (final Throwable e)
                {
                    event.setCancelled(true);
                    event.setCancelReason(message(this.apiMessages, "kick.generic_error", "failed to load player data: " + e));
                    e.printStackTrace();
                    return;
                }

                if (joiningPolicy == JoiningPolicy.NOBODY)
                {
                    event.setCancelled(true);
                    event.setCancelReason(message(this.apiMessages, "join.access_locked"));
                }
                else if (joiningPolicy == JoiningPolicy.ONLY_ADMIN && ! player.get().hasPermission("join.admin")) // wpuszczanie tylko adminów
                {
                    event.setCancelled(true);
                    event.setCancelReason(message(this.apiMessages, "join.access_locked"));
                }
                else if (joiningPolicy == JoiningPolicy.ONLY_VIP && ! player.get().hasPermission("join.vip"))
                {
                    event.setCancelled(true);
                    event.setCancelReason(message(this.apiMessages, "join.access_locked"));
                }
                else if (this.networkManager.onlinePlayersCount() > this.networkManager.getNetworkMeta().get().displayMaxPlayers && ! player.get().hasPermission("join.bypass"))
                {
                    event.setCancelled(true);
                    event.setCancelReason(message(this.apiMessages, "join.server_full"));
                }

                if (event.isCancelled())
                {
                    player.delete(); // delete player data if event is cancelled.
                }
            }
            finally
            {
                event.completeIntent(this.bungeeApiCore.getBungeePlugin());
            }
        });
    }

    @EventHandler
    public void postJoin(final PostLoginEvent event)
    {
        this.networkManager.getOnlinePlayer(event.getPlayer().getName()).expire(-1);
    }

    @EventHandler
    public void onLeave(final PlayerDisconnectEvent event)
    {
        final Value<IOnlinePlayer> player = this.bungeeApiCore.getNetworkManager().getOnlinePlayer(event.getPlayer().getName());
        try
        {
            player.lock();
            this.playersDao.savePlayer(player.getWithoutCache());
            player.delete();
        }
        finally
        {
            player.unlock();
        }
    }
    
    @EventHandler
    public void onServerChange(final ServerSwitchEvent event)
    {
        final Value<IOnlinePlayer> player = this.bungeeApiCore.getNetworkManager().getOnlinePlayer(event.getPlayer().getName());
        try
        {
            player.lock();
            final IOnlinePlayer iOnlinePlayer = player.getWithoutCache();
            iOnlinePlayer.setServerId(UUID.fromString(event.getPlayer().getServer().getInfo().getName()));
            player.set(iOnlinePlayer); // send new data to redis
        }
        catch (final IllegalArgumentException ex)
        {
            this.bungeeApiCore.getLogger().log(Level.SEVERE, "Can't set player's serverId in onServerChange", ex);
        }
        finally
        {
            player.unlock();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
