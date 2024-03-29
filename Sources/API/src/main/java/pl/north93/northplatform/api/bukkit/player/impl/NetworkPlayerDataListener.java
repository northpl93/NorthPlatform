package pl.north93.northplatform.api.bukkit.player.impl;


import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import com.destroystokyo.paper.event.player.PlayerInitialSpawnEvent;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.permissions.PermissionsInjector;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.player.event.PlayerDataLoadedEvent;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.players.IOnlinePlayer;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.global.network.server.joinaction.IServerJoinAction;
import pl.north93.northplatform.api.global.network.server.joinaction.JoinActionsContainer;
import pl.north93.northplatform.api.global.permissions.Group;
import pl.north93.northplatform.api.global.redis.observable.IObservationManager;
import pl.north93.northplatform.api.global.redis.observable.Value;

@Slf4j
public class NetworkPlayerDataListener implements AutoListener
{
    private static final String PLAYER_DATA_NOT_LOADED = ChatColor.RED + "Player data isn't loaded";
    @Inject
    private IBukkitServerManager serverManager;
    @Inject
    private IObservationManager observation;
    @Inject
    private IPlayersManager playersManager;

    private final Multimap<UUID, IServerJoinAction> cachedJoinActions = HashMultimap.create();

    @EventHandler(priority = EventPriority.MONITOR)
    public void asyncPlayerDataPreCache(final AsyncPlayerPreLoginEvent event)
    {
        if (event.getLoginResult() != Result.ALLOWED)
        {
            log.info("Skipped loading data of {}, because login is cancelled.", event.getName());
            return;
        }

        final Identity identity = Identity.create(event.getUniqueId(), event.getName());

        // wymuszamy pobranie danych i dodatkowo weryfikujemy czy one rzeczywiscie tu sa
        final IOnlinePlayer onlinePlayer = this.playersManager.unsafe().getOnlineValue(identity.getNick()).get();
        if (onlinePlayer == null)
        {
            log.error("Player {} ({}) data is null in asyncPlayerDataPreCache, cancelling login", identity.getNick(), identity.getUuid());
            event.setLoginResult(Result.KICK_OTHER);
            return;
        }

        // pobieramy liste akcji do wykonania po wejsciu na serwer
        this.cachedJoinActions.replaceValues(identity.getUuid(), this.fetchActions(identity));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void earlyHandleLoadedData(final PlayerInitialSpawnEvent event)
    {
        final NorthPlayerImpl player = (NorthPlayerImpl) INorthPlayer.wrap(event.getPlayer());
        if (this.abortLoginIfNoPlayerData(player))
        {
            // brak danych gracza; zapobiegamy dalszym wyjatkom
            return;
        }

        LanguageKeeper.updateLocale(player, player.getMyLocale()); // now getMyLocale will have 0 latency, because player data is precached

        // injectujemy nasz szystem uprawnien
        PermissionsInjector.inject(player.getCraftPlayer());
        final PermissionAttachment attachment = player.addAttachment(this.serverManager.getPlugin());

        final Group group = player.getGroup();
        this.addPermissions(attachment, group);

        // zmieniamy startowy display name na ten poprawny popbrany z sieci
        final IOnlinePlayer onlinePlayer = player.getValue().get();
        if (onlinePlayer.hasDisplayName())
        {
            player.setDisplayName(onlinePlayer.getDisplayName());
        }

        // wykonujemy akcje przed zespawnowaniem entity gracza
        for (final IServerJoinAction action : this.cachedJoinActions.get(player.getUniqueId()))
        {
            action.playerPreSpawn(player, event.getSpawnLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void lateHandleLoadedData(final PlayerJoinEvent event)
    {
        final INorthPlayer player = INorthPlayer.wrap(event.getPlayer());
        if (this.abortLoginIfNoPlayerData(player))
        {
            // brak danych gracza; zapobiegamy dalszym wyjatkom
            return;
        }

        event.setJoinMessage(null); // hide bukkit's join message
        final Group group = player.getGroup();
        if (! StringUtils.isEmpty(group.getJoinMessage())) // send our message
        {
            Bukkit.broadcastMessage(MessageFormat.format(group.getJoinMessage(), player.getName()));
        }

        // wykonujemy stary event obslugujacy zaladowanie danych gracza
        final Collection<IServerJoinAction> joinActions = this.cachedJoinActions.get(player.getUniqueId());
        this.serverManager.callEvent(new PlayerDataLoadedEvent(player, joinActions));

        // wykonujemy akcje przy standardowym evencie wejscia gracza
        for (final IServerJoinAction action : joinActions)
        {
            action.playerJoined(player);
        }
        this.cachedJoinActions.removeAll(player.getUniqueId());
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
                attachment.setPermission(permission.substring(1), false);
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

    private Collection<IServerJoinAction> fetchActions(final Identity identity)
    {
        final Value<JoinActionsContainer> actions = this.observation.get(JoinActionsContainer.class, "serveractions:" + identity.getNick());

        final JoinActionsContainer joinActionsContainer = actions.getAndDelete();
        if (joinActionsContainer == null || joinActionsContainer.isInvalidServer(this.serverManager.getServerId()))
        {
            // jesli nie ma zadnych akcji lub UUID serwera sie nie zgadza to zwracamy pusta liste
            return Collections.emptyList();
        }

        return Arrays.asList(joinActionsContainer.getServerJoinActions());
    }

    private boolean abortLoginIfNoPlayerData(final INorthPlayer player)
    {
        final NorthPlayerImpl impl = (NorthPlayerImpl) player;
        if (impl.getValue().isPreset())
        {
            return false;
        }

        log.error("Player {} ({}) data is null in abortLoginIfNoPlayerData, cancelling join", player.getName(), player.getUniqueId());
        player.kickPlayer(PLAYER_DATA_NOT_LOADED);
        return true;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
