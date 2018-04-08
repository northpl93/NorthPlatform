package pl.north93.zgame.api.bungee.proxy.impl;

import static net.md_5.bungee.api.ChatColor.RED;


import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.reflections.DioriteReflectionUtils;
import org.diorite.commons.reflections.FieldAccessor;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import pl.north93.zgame.api.bungee.BungeeApiCore;
import pl.north93.zgame.api.bungee.Main;
import pl.north93.zgame.api.bungee.proxy.event.HandlePlayerProxyJoinEvent;
import pl.north93.zgame.api.bungee.proxy.event.HandlePlayerProxyQuitEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.event.PlayerJoinNetEvent;
import pl.north93.zgame.api.global.network.event.PlayerQuitNetEvent;
import pl.north93.zgame.api.global.network.impl.OnlinePlayerImpl;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
import pl.north93.zgame.api.global.network.players.IPlayersManager.IPlayersDataManager;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.network.players.NameSizeMistakeException;
import pl.north93.zgame.api.global.network.players.UsernameDetails;
import pl.north93.zgame.api.global.redis.event.IEventManager;
import pl.north93.zgame.api.global.redis.observable.Lock;
import pl.north93.zgame.api.global.redis.observable.Value;

public class PlayerListener implements Listener
{
    private static final Pattern  NICK_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");
    @Inject
    private BungeeApiCore   bungeeApiCore;
    @Inject @Messages("Messages")
    private MessagesBox     apiMessages;
    @Inject
    private INetworkManager networkManager;
    @Inject
    private IEventManager   eventManager;

    @EventHandler
    public void onLogin(final PreLoginEvent event)
    {
        final PendingConnection connection = event.getConnection();
        this.runIntent(event, () ->
        {
            final String nick = connection.getName();
            if (! NICK_PATTERN.matcher(nick).matches())
            {
                event.setCancelled(true);
                event.setCancelReason(RED + this.apiMessages.getMessage("join.invalid_nick"));
                return;
            }

            final Optional<UsernameDetails> details = this.networkManager.getPlayers().getCache().getNickDetails(nick);
            if (! details.isPresent())
            {
                event.setCancelled(true);
                event.setCancelReason(RED + this.apiMessages.getMessage("join.premium.check_failed"));
                return;
            }

            final UsernameDetails usernameDetails = details.get();
            if (usernameDetails.isPremium() && !usernameDetails.getValidSpelling().equals(nick))
            {
                event.setCancelled(true);
                event.setCancelReason(RED + this.apiMessages.getMessage("join.premium.name_size_mistake"));
                return;
            }

            if (this.networkManager.getPlayers().isOnline(nick)) // sprawdzanie czy taki gracz juz jest w sieci
            {
                event.setCancelled(true);
                event.setCancelReason(RED + this.apiMessages.getMessage("join.already_online"));
                return;
            }

            connection.setOnlineMode(usernameDetails.isPremium());
        });
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(final LoginEvent event)
    {
        final PendingConnection conn = event.getConnection();
        this.runIntent(event, () ->
        {
            final Value<OnlinePlayerImpl> player;
            try
            {
                final IPlayersDataManager dataManager = this.networkManager.getPlayers().getInternalData();
                final String proxyName = this.bungeeApiCore.getProxyConfig().getUniqueName();

                player = dataManager.loadPlayer(conn.getUniqueId(), conn.getName(), conn.isOnlineMode(), proxyName);
                player.expire(5); // po 5 sekundach logowanie wygasnie
            }
            catch (final NameSizeMistakeException e)
            {
                event.setCancelled(true);
                event.setCancelReason(this.apiMessages.getMessage("pl-PL", "join.name_size_mistake", e.getNick(), conn.getName()));
                return;
            }
            catch (final Throwable e)
            {
                event.setCancelled(true);
                event.setCancelReason(this.apiMessages.getMessage("pl-PL", "kick.generic_error", "failed to load player data: " + e));
                e.printStackTrace();
                return;
            }

            //noinspection unchecked
            final HandlePlayerProxyJoinEvent joinEvent = this.bungeeApiCore.callEvent(new HandlePlayerProxyJoinEvent(conn, (Value) player));
            if (joinEvent.isCancelled())
            {
                player.delete(); // delete player data if event is cancelled.
                event.setCancelled(true);
                event.setCancelReason(joinEvent.getCancelReason());
            }
        });
    }

    @EventHandler
    public void postJoin(final PostLoginEvent event)
    {
        final IPlayersManager.Unsafe unsafe = this.networkManager.getPlayers().unsafe();
        final Value<IOnlinePlayer> onlineValue = unsafe.getOnline(event.getPlayer().getName());

        if (! onlineValue.expire(-1))
        {
            // nastapilo przedawnienie logowania, rozlaczamy gracza aby uniknac dalszego
            // bugowania sie systemu.
            event.getPlayer().disconnect();
            return;
        }

        // wywolujemy sieciowy event dolaczenia gracza
        this.eventManager.callEvent(new PlayerJoinNetEvent((OnlinePlayerImpl) onlineValue.get()));
    }

    @EventHandler
    public void onLeave(final PlayerDisconnectEvent event)
    {
        final ProxiedPlayer proxyPlayer = event.getPlayer();
        final Value<IOnlinePlayer> player = this.networkManager.getPlayers().unsafe().getOnline(proxyPlayer.getName());

        // wywołujemy event w którym spokojnie można obsłużyć wyjście gracza
        this.bungeeApiCore.callEvent(new HandlePlayerProxyQuitEvent(proxyPlayer, player));

        try (final Lock lock = player.lock())
        {
            final IOnlinePlayer onlinePlayer = player.getWithoutCache();
            if (onlinePlayer == null)
            {
                this.bungeeApiCore.getLogger().warning("onlinePlayer==null in onLeave. " + event);
                return;
            }

            this.eventManager.callEvent(new PlayerQuitNetEvent((OnlinePlayerImpl) onlinePlayer));
            this.networkManager.getPlayers().getInternalData().savePlayer(onlinePlayer);
            player.delete();
        }
    }
    
    @EventHandler
    public void onServerChange(final ServerSwitchEvent event)
    {
        final Logger logger = this.bungeeApiCore.getLogger();

        final IPlayersManager playersManager = this.networkManager.getPlayers();
        try (final IPlayerTransaction transaction = playersManager.transaction(Identity.of(event.getPlayer())))
        {
            if (! transaction.isOnline())
            {
                logger.log(Level.WARNING, "Player {0} is offline in onServerChange", transaction.getPlayer().getUuid());
                return;
            }

            final IOnlinePlayer onlinePlayer = (IOnlinePlayer) transaction.getPlayer();
            onlinePlayer.setServerId(UUID.fromString(event.getPlayer().getServer().getInfo().getName()));
            this.updateLocale(event.getPlayer(), onlinePlayer.getMyLocale());
        }
        catch (final Exception e)
        {
            logger.log(Level.SEVERE, "Can't set player's serverId in onServerChange", e);
        }
    }

    private <T> void runIntent(final AsyncEvent<T> event, final Runnable task)
    {
        final Main plugin = this.bungeeApiCore.getBungeePlugin();

        event.registerIntent(plugin);
        this.bungeeApiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            try
            {
                task.run();
            }
            finally
            {
                event.completeIntent(plugin);
            }
        });
    }

    private static final FieldAccessor<Locale> player_locale = DioriteReflectionUtils.getField("net.md_5.bungee.UserConnection", "locale", Locale.class);
    private void updateLocale(final ProxiedPlayer player, final Locale newLocale)
    {
        player_locale.set(player, newLocale);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
