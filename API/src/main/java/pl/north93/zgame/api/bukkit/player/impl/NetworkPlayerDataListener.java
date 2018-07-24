package pl.north93.zgame.api.bukkit.player.impl;

import static pl.north93.zgame.api.bukkit.player.impl.LanguageKeeper.updateLocale;


import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import com.destroystokyo.paper.event.player.PlayerInitialSpawnEvent;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.permissions.PermissionsInjector;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.player.event.PlayerDataLoadedEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.network.server.joinaction.IServerJoinAction;
import pl.north93.zgame.api.global.network.server.joinaction.JoinActionsContainer;
import pl.north93.zgame.api.global.permissions.Group;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;

@Slf4j
public class NetworkPlayerDataListener implements Listener
{
    @Inject
    private BukkitApiCore       apiCore;
    @Inject
    private IObservationManager observation;
    @Inject
    private INetworkManager     networkManager;

    private final Multimap<UUID, IServerJoinAction> cachedJoinActions = HashMultimap.create();

    @EventHandler(priority = EventPriority.MONITOR)
    public void asyncPlayerDataPreCache(final AsyncPlayerPreLoginEvent event)
    {
        if (event.getLoginResult() != Result.ALLOWED)
        {
            log.info("Skipped loading data of {}, because login is cancelled.");
            return;
        }

        final Identity identity = Identity.create(event.getUniqueId(), event.getName());

        // wymuszamy pobranie danych i dodatkowo weryfikujemy czy one rzeczywiscie tu sa
        final IOnlinePlayer iOnlinePlayer = this.networkManager.getPlayers().unsafe().getOnlineValue(identity.getNick()).get();
        if (iOnlinePlayer == null)
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
        updateLocale(player, player.getMyLocale()); // now getMyLocale will have 0 latency, because player data is precached

        // injectujemy nasz szystem uprawnien
        PermissionsInjector.inject(player.getCraftPlayer());
        final PermissionAttachment attachment = player.addAttachment(this.apiCore.getPluginMain());

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
        final Group group = player.getGroup();

        event.setJoinMessage(null); // hide bukkit's join message
        if (! StringUtils.isEmpty(group.getJoinMessage())) // send our message
        {
            Bukkit.broadcastMessage(MessageFormat.format(group.getJoinMessage(), player.getName()));
        }

        // wykonujemy stary event obslugujacy zaladowanie danych gracza
        final Collection<IServerJoinAction> joinActions = this.cachedJoinActions.get(player.getUniqueId());
        this.apiCore.callEvent(new PlayerDataLoadedEvent(player, joinActions));

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

    private Collection<IServerJoinAction> fetchActions(final Identity identity)
    {
        final Value<JoinActionsContainer> actions = this.observation.get(JoinActionsContainer.class, "serveractions:" + identity.getNick());
        final JoinActionsContainer joinActionsContainer = actions.getAndDelete();
        if (joinActionsContainer == null)
        {
            return Collections.emptyList();
        }
        return Arrays.asList(joinActionsContainer.getServerJoinActions());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
