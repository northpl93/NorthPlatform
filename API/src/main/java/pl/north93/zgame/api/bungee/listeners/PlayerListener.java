package pl.north93.zgame.api.bungee.listeners;

import static net.md_5.bungee.api.ChatColor.RED;
import static pl.north93.zgame.api.global.API.message;


import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.impl.OnlinePlayerImpl;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;

public class PlayerListener implements Listener
{
    private static final MetaKey  BAN_EXPIRE   = MetaKey.get("banExpireAt");
    private static final Pattern  NICK_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");
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
                final String nick = connection.getName();
                if (! NICK_PATTERN.matcher(nick).matches())
                {
                    event.setCancelled(true);
                    event.setCancelReason(RED + this.apiMessages.getString("join.invalid_nick"));
                    return;
                }

                final Optional<UsernameDetails> details = this.bungeeApiCore.getUsernameCache().getUsernameDetails(nick);

                if (! details.isPresent())
                {
                    event.setCancelled(true);
                    event.setCancelReason(RED + this.apiMessages.getString("join.premium.check_failed"));
                    return;
                }

                final UsernameDetails usernameDetails = details.get();
                if (usernameDetails.isPremium() && !usernameDetails.getValidSpelling().equals(nick))
                {
                    event.setCancelled(true);
                    event.setCancelReason(RED + this.apiMessages.getString("join.premium.name_size_mistake"));
                    return;
                }

                if (this.bungeeApiCore.getNetworkManager().getPlayers().isOnline(nick)) // sprawdzanie czy taki gracz juz jest w sieci
                {
                    event.setCancelled(true);
                    event.setCancelReason(RED + this.apiMessages.getString("join.already_online"));
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

        final PendingConnection conn = event.getConnection();
        this.bungeeApiCore.getPlatformConnector().runTaskAsynchronously(() -> // run async task
        {
            try
            {
                final JoiningPolicy joiningPolicy = this.bungeeApiCore.getNetworkManager().getJoiningPolicy();

                final Value<OnlinePlayerImpl> player;
                try
                {
                    player = this.playersDao.loadPlayer(conn.getUniqueId(), conn.getName(), conn.isOnlineMode(), this.bungeeApiCore.getProxyConfig().getUniqueName());
                    player.expire(5);
                }
                catch (final NameSizeMistakeException e)
                {
                    event.setCancelled(true);
                    event.setCancelReason(message(this.apiMessages, "join.name_size_mistake", e.getNick(), conn.getName()));
                    return;
                }
                catch (final Throwable e)
                {
                    event.setCancelled(true);
                    event.setCancelReason(message(this.apiMessages, "kick.generic_error", "failed to load player data: " + e));
                    e.printStackTrace();
                    return;
                }

                final OnlinePlayerImpl cache = player.get();
                if (joiningPolicy == JoiningPolicy.NOBODY)
                {
                    event.setCancelled(true);
                    event.setCancelReason(message(this.apiMessages, "join.access_locked"));
                }
                else if (joiningPolicy == JoiningPolicy.ONLY_ADMIN && ! cache.hasPermission("join.admin")) // wpuszczanie tylko adminów
                {
                    event.setCancelled(true);
                    event.setCancelReason(message(this.apiMessages, "join.access_locked"));
                }
                else if (joiningPolicy == JoiningPolicy.ONLY_VIP && ! cache.hasPermission("join.vip"))
                {
                    event.setCancelled(true);
                    event.setCancelReason(message(this.apiMessages, "join.access_locked"));
                }
                else if (cache.isBanned())
                {
                    if (cache.getMetaStore().contains(BAN_EXPIRE) && System.currentTimeMillis() > cache.getMetaStore().getLong(BAN_EXPIRE))
                    {
                        player.update(p ->
                        {
                            p.setBanned(false);
                            p.getMetaStore().remove(BAN_EXPIRE);
                        });
                    }
                    else
                    {
                        event.setCancelled(true);
                        event.setCancelReason(message(this.apiMessages, "join.banned"));
                    }
                }
                else if (this.networkManager.getPlayers().onlinePlayersCount() > this.networkManager.getNetworkMeta().get().displayMaxPlayers && ! cache.hasPermission("join.bypass"))
                {
                    event.setCancelled(true);
                    event.setCancelReason(message(this.apiMessages, "join.server_full"));
                }

                if (event.isCancelled())
                {
                    player.delete(); // delete player data if event is cancelled.
                    return;
                }

                final String hostAddress = conn.getAddress().getHostString();
                this.playersDao.logPlayerJoin(cache.getUuid(), cache.getNick(), cache.isPremium(), hostAddress, cache.getProxyId());
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
        this.networkManager.getPlayers().unsafe().getOnline(event.getPlayer().getName()).expire(-1);
    }

    @EventHandler
    public void onLeave(final PlayerDisconnectEvent event)
    {
        final Value<IOnlinePlayer> player = this.bungeeApiCore.getNetworkManager().getPlayers().unsafe().getOnline(event.getPlayer().getName());

        try
        {
            player.lock();
            final IOnlinePlayer onlinePlayer = player.getWithoutCache();
            if (onlinePlayer == null)
            {
                this.bungeeApiCore.getLogger().warning("onlinePlayer==null in onLeave. " + event);
                return;
            }
            this.playersDao.savePlayer(onlinePlayer);
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
        final Value<IOnlinePlayer> player = this.bungeeApiCore.getNetworkManager().getPlayers().unsafe().getOnline(event.getPlayer().getName());

        try
        {
            player.lock();
            final IOnlinePlayer iOnlinePlayer = player.getWithoutCache();
            if (iOnlinePlayer != null)
            {
                iOnlinePlayer.setServerId(UUID.fromString(event.getPlayer().getServer().getInfo().getName()));
                player.set(iOnlinePlayer); // send new data to redis
            }
            else
            {
                this.bungeeApiCore.getLogger().warning("iOnlinePlayer==null in onServerChange. " + event);
            }
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
