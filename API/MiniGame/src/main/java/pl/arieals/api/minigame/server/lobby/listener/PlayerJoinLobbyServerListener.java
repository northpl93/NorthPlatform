package pl.arieals.api.minigame.server.lobby.listener;

import static java.text.MessageFormat.format;


import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.arieals.api.minigame.server.lobby.event.PlayerSwitchedHubEvent;
import pl.arieals.api.minigame.server.lobby.hub.SelectHubServerJoinAction;
import pl.north93.zgame.api.bukkit.player.event.PlayerDataLoadedEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.server.joinaction.IServerJoinAction;

public class PlayerJoinLobbyServerListener implements Listener
{
    @Inject
    private MiniGameServer gameServer;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void teleportPlayerToDefaultHub(final PlayerDataLoadedEvent event)
    {
        if (this.containsLobbySwitchAction(event.getJoinActions()))
        {
            return;
        }

        final LobbyManager serverManager = this.gameServer.getServerManager();
        serverManager.getLocalHub().movePlayerToDefaultHub(event.getNorthPlayer());
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
    public void test(final PlayerSwitchedHubEvent event)
    {
        final String playerName = event.getPlayer().getName();
        final String hubId = event.getNewHub().getHubId();
        Bukkit.broadcastMessage(format("Player {0} switched hub to {1}", playerName, hubId));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
