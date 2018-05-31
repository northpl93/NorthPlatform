package pl.arieals.api.minigame.server.lobby.hub.listener;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.arieals.api.minigame.server.lobby.hub.HubWorld;
import pl.arieals.api.minigame.server.lobby.hub.LocalHubServer;
import pl.arieals.api.minigame.server.lobby.hub.SelectHubServerJoinAction;
import pl.arieals.api.minigame.server.lobby.hub.event.PlayerSwitchedHubEvent;
import pl.arieals.api.minigame.shared.api.status.IPlayerStatusManager;
import pl.arieals.api.minigame.shared.api.status.InHubStatus;
import pl.north93.zgame.api.bukkit.player.event.PlayerDataLoadedEvent;
import pl.north93.zgame.api.bukkit.server.IBukkitExecutor;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.network.server.joinaction.IServerJoinAction;

/**
 * Ogólna obsługa wejścia gracza na serwer hubów,
 * dodatkowe śledzenie aktualnego hubu gracza.
 */
public class PlayerJoinHubServerListener implements AutoListener
{
    @Inject
    private MiniGameServer       gameServer;
    @Inject
    private IBukkitExecutor      bukkitExecutor;
    @Inject
    private IPlayerStatusManager statusManager;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void teleportPlayerToDefaultHubOnJoin(final PlayerDataLoadedEvent event)
    {
        if (this.containsLobbySwitchAction(event.getJoinActions()))
        {
            return;
        }

        final LobbyManager serverManager = this.gameServer.getServerManager();
        serverManager.getLocalHub().movePlayerToDefaultHub(event.getPlayer());
    }

    private boolean containsLobbySwitchAction(final Collection<IServerJoinAction> actions)
    {
        for (final IServerJoinAction action : actions)
        {
            if (action instanceof SelectHubServerJoinAction)
            {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void callSwitchedHubEventWhenTeleport(final PlayerTeleportEvent event)
    {
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (event.getCause() == TeleportCause.PLUGIN || from.getWorld() == to.getWorld())
        {
            return;
        }

        final LobbyManager serverManager = this.gameServer.getServerManager();
        final LocalHubServer localHub = serverManager.getLocalHub();

        final HubWorld oldHubWorld = localHub.getHubWorld(from.getWorld());
        final HubWorld newWorldHub = localHub.getHubWorld(to.getWorld());

        Bukkit.getPluginManager().callEvent(new PlayerSwitchedHubEvent(event.getPlayer(), oldHubWorld, newWorldHub));
    }

    @EventHandler
    public void updatePlayerStatusOnHubSwitch(final PlayerSwitchedHubEvent event)
    {
        final LobbyManager serverManager = this.gameServer.getServerManager();

        this.bukkitExecutor.async(() ->
        {
            final Identity identity = Identity.of(event.getPlayer());
            final InHubStatus status = new InHubStatus(serverManager.getServerId(), event.getNewHub().getHubId());

            this.statusManager.updatePlayerStatus(identity, status);
        });
//        final String playerName = event.getPlayer().getName();
//        final String hubId = event.getNewHub().getHubId();
//        Bukkit.broadcastMessage(format("Player {0} switched hub to {1}", playerName, hubId));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
