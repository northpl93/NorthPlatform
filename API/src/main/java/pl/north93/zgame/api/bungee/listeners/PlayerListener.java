package pl.north93.zgame.api.bungee.listeners;

import static pl.north93.zgame.api.global.API.message;
import static pl.north93.zgame.api.global.redis.RedisKeys.PLAYERS;


import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.north93.zgame.api.bungee.BungeeApiCore;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.data.StorageConnector;
import pl.north93.zgame.api.global.data.UsernameCache.UsernameDetails;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.NetworkPlayer;
import pl.north93.zgame.api.global.utils.UTF8Control;
import redis.clients.jedis.Jedis;

public class PlayerListener implements Listener
{
    private final ResourceBundle apiMessages = ResourceBundle.getBundle("Messages", new UTF8Control());
    private final BungeeApiCore bungeeApiCore;

    public PlayerListener(final BungeeApiCore bungeeApiCore)
    {
        this.bungeeApiCore = bungeeApiCore;
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

    @EventHandler
    public void onJoin(final PostLoginEvent event)
    {
        final ProxiedPlayer proxyPlayer = event.getPlayer();
        final JoiningPolicy joiningPolicy = this.bungeeApiCore.getNetworkManager().getJoiningPolicy();
        final Optional<UsernameDetails> details = this.bungeeApiCore.getUsernameCache().getUsernameDetails(event.getPlayer().getName());

        if (! details.isPresent())
        {
            proxyPlayer.disconnect(TextComponent.fromLegacyText(this.apiMessages.getString("join.username_details_not_present")));
            return;
        }

        if (joiningPolicy == JoiningPolicy.NOBODY) // blokada wpuszczania wszystkich
        {
            proxyPlayer.disconnect(TextComponent.fromLegacyText(this.apiMessages.getString("join.access_locked")));
            return;
        }

        NetworkPlayer player = this.bungeeApiCore.getPlayersDao().loadPlayer(proxyPlayer.getUniqueId(), proxyPlayer.getName());
        if (player == null)
        {
            player = new NetworkPlayer();
            player.setNick(proxyPlayer.getName());
            player.setUuid(proxyPlayer.getUniqueId());
            player.setGroup(this.bungeeApiCore.getPermissionsManager().getDefaultGroup());
        }
        else
        {
            if (! player.getNick().equals(proxyPlayer.getName()))
            {
                proxyPlayer.disconnect(TextComponent.fromLegacyText(message(this.apiMessages, "join.name_size_mistake", player.getNick(), proxyPlayer.getName())));
                return;
            }
        }

        if (joiningPolicy == JoiningPolicy.ONLY_ADMIN && !player.hasPermission("join.admin")) // wpuszczanie tylko adminów
        {
            proxyPlayer.disconnect(TextComponent.fromLegacyText(message(this.apiMessages, "join.access_locked")));
            return;
        }

        player.setPremium(details.get().isPremium());
        player.setProxyId(this.bungeeApiCore.getProxyConfig().getUniqueName());
        player.setServerId(UUID.randomUUID()); // It will be filled in onServerChange
        player.sendUpdate(); // send data to Redis
    }

    @EventHandler
    public void onLeave(final PlayerDisconnectEvent event)
    {
        final StorageConnector storageConnector = API.getApiCore().getComponentManager().getComponent("API.Database.StorageConnector");
        try (final Jedis jedis = storageConnector.getJedisPool().getResource())
        {
            jedis.del(PLAYERS + event.getPlayer().getName().toLowerCase(Locale.ROOT));
        }
    }

    @EventHandler
    public void onServerChange(final ServerSwitchEvent event)
    {
        final NetworkPlayer player = this.bungeeApiCore.getNetworkManager().getNetworkPlayer(event.getPlayer().getName());
        if (player == null)
        {
            event.getPlayer().disconnect(TextComponent.fromLegacyText(message(this.apiMessages, "kick.generic_error", "player==null in onServerChange")));
            return;
        }

        try
        {
            player.setServerId(UUID.fromString(event.getPlayer().getServer().getInfo().getName()));
        }
        catch (final IllegalArgumentException ex)
        {
            this.bungeeApiCore.getLogger().log(Level.SEVERE, "Can't set player's serverId in onServerChange", ex);
        }
        player.sendUpdate(); // send new data to redis
    }
}
