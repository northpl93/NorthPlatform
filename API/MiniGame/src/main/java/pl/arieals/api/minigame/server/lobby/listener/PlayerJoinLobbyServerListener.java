package pl.arieals.api.minigame.server.lobby.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class PlayerJoinLobbyServerListener implements Listener
{
    @Inject
    private MiniGameServer gameServer;

    @EventHandler
    public void teleportPlayerToDefaultHub(final PlayerJoinEvent event)
    {
        final LobbyManager serverManager = this.gameServer.getServerManager();
        serverManager.getLocalHub().movePlayerToDefaultHub(event.getPlayer());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
