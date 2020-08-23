package pl.north93.northplatform.api.minigame.server.lobby;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.server.IServerManager;
import pl.north93.northplatform.api.minigame.server.lobby.hub.LocalHubServer;
import pl.north93.northplatform.api.minigame.shared.api.hub.IHubServer;

@Slf4j
public class LobbyManager implements IServerManager
{
    @Inject
    private LobbyHubsManager lobbyHubsManager;
    @Inject
    private BukkitApiCore apiCore;

    @Bean
    private LobbyManager()
    {
    }

    @Override
    public void start()
    {
        log.info("Hub component started successfully");
    }

    @Override
    public void stop()
    {
        this.lobbyHubsManager.unregister();
    }

    @Override
    public UUID getServerId()
    {
        return this.apiCore.getServerId();
    }

    @Override
    public void tpToHub(final Iterable<? extends Player> players, final String hubId)
    {
        this.lobbyHubsManager.tpToHub(players, hubId);
    }

    @Override
    public void tpToHub(final Iterable<? extends Player> players, final IHubServer hubServer, final String hubId)
    {
        this.lobbyHubsManager.tpToHub(players, hubServer, hubId);
    }

    /**
     * Zwraca instancje reprezentujaca i zarzadzajaca tym serwerem z hubami.
     *
     * @return LocalHub.
     */
    public LocalHubServer getLocalHub()
    {
        return this.lobbyHubsManager.getLocalHub();
    }

    /**
     * Zwraca wszystkie uruchomione w sieci serwery hubów.
     *
     * @return Lista wszystkich serwerów hubów.
     */
    public Collection<? extends IHubServer> getAllHubServers()
    {
        return this.lobbyHubsManager.getAllHubServers();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
