package pl.arieals.api.minigame.server.lobby;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.IServerManager;
import pl.arieals.api.minigame.server.lobby.hub.LocalHub;
import pl.arieals.api.minigame.server.lobby.listener.PlayerJoinLobbyServerListener;
import pl.arieals.api.minigame.shared.api.hub.RemoteHub;
import pl.arieals.api.minigame.shared.impl.HubsManager;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class LobbyManager implements IServerManager
{
    @Inject
    private Logger        logger;
    @Inject
    private BukkitApiCore apiCore;
    @Inject
    private HubsManager   hubsManager;
    private LocalHub      localHub;

    @Override
    public void start()
    {
        this.localHub = new LocalHub();
        this.hubsManager.setHub(new RemoteHub(this.localHub));
        this.localHub.refreshConfiguration();

        this.apiCore.registerEvents(
                new PlayerJoinLobbyServerListener() // nasluchuje wejscia gracza na serwer z hubami
        );

        this.logger.log(Level.INFO, "Hub component started successfully");
    }

    @Override
    public void stop()
    {
        this.hubsManager.removeHub(this.localHub.getServerId());
    }

    @Override
    public void tpToHub(final Collection<Player> players, final String hubId)
    {
        for (final Player player : players)
        {
            this.localHub.movePlayerToHub(player, hubId);
        }
    }

    /**
     * Zwraca instancje reprezentujaca i zarzadzajaca tym serwerem z hubami.
     *
     * @return LocalHub.
     */
    public LocalHub getLocalHub()
    {
        return this.localHub;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
