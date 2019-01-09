package pl.north93.northplatform.api.bungee.proxy.impl.listener;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.reflections.DioriteReflectionUtils;
import org.diorite.commons.reflections.FieldAccessor;

import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;
import net.md_5.bungee.api.event.LoginAbortedEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import pl.north93.northplatform.api.bungee.proxy.event.HandlePlayerProxyJoinEvent;
import pl.north93.northplatform.api.bungee.proxy.event.HandlePlayerProxyQuitEvent;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.network.event.PlayerJoinNetEvent;
import pl.north93.northplatform.api.global.network.event.PlayerQuitNetEvent;
import pl.north93.northplatform.api.global.network.impl.players.OnlinePlayerImpl;
import pl.north93.northplatform.api.global.network.mojang.UsernameDetails;
import pl.north93.northplatform.api.global.network.players.IOfflinePlayer;
import pl.north93.northplatform.api.global.network.players.IOnlinePlayer;
import pl.north93.northplatform.api.global.network.players.IPlayerTransaction;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.global.network.players.NameSizeMistakeException;
import pl.north93.northplatform.api.global.redis.event.IEventManager;
import pl.north93.northplatform.api.global.redis.observable.IObservationManager;
import pl.north93.northplatform.api.global.redis.observable.Lock;
import pl.north93.northplatform.api.global.redis.observable.Value;
import pl.north93.northplatform.api.bungee.BungeeApiCore;
import pl.north93.northplatform.api.bungee.Main;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.mojang.IMojangCache;

/**
 * Zarządza ładowaniem danych gracza i usuwaniem ich z Redisa.
 */
@Slf4j
public class PlayerNetworkListener implements Listener
{
    private static final Pattern             NICK_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");
    @Inject @Messages("Messages")
    private              MessagesBox         apiMessages;
    @Inject
    private              BungeeApiCore       bungeeApiCore;
    @Inject
    private              IObservationManager observer;
    @Inject
    private              IEventManager       eventManager;
    @Inject
    private              IMojangCache        mojangCache;
    @Inject
    private              IPlayersManager     playersManager;

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
                event.setCancelReason(this.apiMessages.getComponent("pl-PL", "join.invalid_nick"));
                return;
            }

            final Optional<UsernameDetails> details = this.mojangCache.getUsernameDetails(nick);
            if (! details.isPresent())
            {
                event.setCancelled(true);
                event.setCancelReason(this.apiMessages.getComponent("pl-PL", "join.premium.check_failed"));
                return;
            }

            final UsernameDetails usernameDetails = details.get();
            if (usernameDetails.getIsPremium() && !usernameDetails.getUsername().equals(nick))
            {
                event.setCancelled(true);
                event.setCancelReason(this.apiMessages.getComponent("pl-PL", "join.premium.name_size_mistake"));
                return;
            }

            if (this.playersManager.isOnline(nick)) // sprawdzanie czy taki gracz juz jest w sieci
            {
                event.setCancelled(true);
                event.setCancelReason(this.apiMessages.getComponent("pl-PL", "join.already_online"));
                return;
            }

            connection.setOnlineMode(usernameDetails.getIsPremium());
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
                final IPlayersManager.IPlayersDataManager dataManager = this.playersManager.getInternalData();
                final String proxyName = this.bungeeApiCore.getProxyConfig().getUniqueName();

                player = dataManager.loadPlayer(conn.getUniqueId(), conn.getName(), conn.isOnlineMode(), proxyName);
            }
            catch (final NameSizeMistakeException e)
            {
                event.setCancelled(true);
                event.setCancelReason(this.apiMessages.getComponent("pl-PL", "join.name_size_mistake", e.getNick(), conn.getName()));
                return;
            }
            catch (final Exception e)
            {
                event.setCancelled(true);
                event.setCancelReason(this.apiMessages.getComponent("pl-PL", "kick.generic_error", "failed to load player data: " + e));
                log.error("Failed to load player data", e);
                return;
            }

            //noinspection unchecked
            final HandlePlayerProxyJoinEvent joinEvent = this.bungeeApiCore.callEvent(new HandlePlayerProxyJoinEvent(conn, (Value) player));
            if (joinEvent.isCancelled())
            {
                player.delete(); // delete player data if event is cancelled.
                event.setCancelled(true);
                event.setCancelReason(joinEvent.getCancelReason());
                log.info("Cancelled user {} login by server", conn.getName());
            }
        });
    }

    @EventHandler
    public void postJoin(final PostLoginEvent event)
    {
        final IPlayersManager.Unsafe unsafe = this.playersManager.unsafe();
        final Value<IOnlinePlayer> onlineValue = unsafe.getOnlineValue(event.getPlayer().getName());

        // wywolujemy sieciowy event dolaczenia gracza
        this.eventManager.callEvent(new PlayerJoinNetEvent((OnlinePlayerImpl) onlineValue.get()));

        log.info("Successfully logged-in user {}", event.getPlayer().getName());
    }

    @EventHandler
    public void onLoginCancelled(final LoginAbortedEvent event)
    {
        final String name = event.getConnection().getName();
        final Value<IOnlinePlayer> onlineValue = this.playersManager.unsafe().getOnlineValue(name);

        onlineValue.delete();
        log.info("User {} cancelled login before post-login", name);
    }

    @EventHandler
    public void onLeave(final PlayerDisconnectEvent event)
    {
        final IPlayersManager players = this.playersManager;
        final ProxiedPlayer proxyPlayer = event.getPlayer();

        final Value<IOnlinePlayer> onlineValue = players.unsafe().getOnlineValue(proxyPlayer.getName());
        final Value<IOfflinePlayer> offlineValue = players.unsafe().getOfflineValue(proxyPlayer.getUniqueId());

        // wywołujemy event w którym spokojnie można obsłużyć wyjście gracza
        this.bungeeApiCore.callEvent(new HandlePlayerProxyQuitEvent(proxyPlayer, onlineValue));

        final Lock multiLock = this.observer.getMultiLock(onlineValue.getLock(), offlineValue.getLock());
        try (final Lock lock = multiLock.lock())
        {
            final IOnlinePlayer onlinePlayer = onlineValue.getWithoutCache();
            if (onlinePlayer == null)
            {
                log.error("onlinePlayer==null in onLeave. " + event);
                return;
            }

            this.eventManager.callEvent(new PlayerQuitNetEvent((OnlinePlayerImpl) onlinePlayer));
            players.getInternalData().savePlayer(onlinePlayer);
            onlineValue.delete();
        }
        catch (final Exception e)
        {
            log.error("Failed to save player data for {}/{}", proxyPlayer.getName(), proxyPlayer.getUniqueId(), e);
        }
    }
    
    @EventHandler
    public void onServerChange(final ServerSwitchEvent event)
    {
        try (final IPlayerTransaction transaction = this.playersManager.transaction(Identity.of(event.getPlayer())))
        {
            if (transaction.isOffline())
            {
                log.warn("Player {} is offline in onServerChange", transaction.getPlayer().getUuid());
                return;
            }

            final IOnlinePlayer onlinePlayer = (IOnlinePlayer) transaction.getPlayer();
            onlinePlayer.setServerId(UUID.fromString(event.getPlayer().getServer().getInfo().getName()));
            this.updateLocale(event.getPlayer(), onlinePlayer.getMyLocale());
        }
        catch (final Exception e)
        {
            log.error("Can't set player's serverId in onServerChange", e);
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
            catch (final Exception e)
            {
                log.error("An exception has been thrown while handling asynchronous login task", e);
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
