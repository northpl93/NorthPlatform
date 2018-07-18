package pl.arieals.api.minigame.server.lobby;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.arieals.api.minigame.server.IServerManager;
import pl.arieals.api.minigame.server.lobby.hub.LocalHubServer;
import pl.arieals.api.minigame.shared.api.hub.IHubServer;
import pl.arieals.api.minigame.shared.api.status.InHubStatus;
import pl.arieals.api.minigame.shared.api.status.IPlayerStatus;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class LobbyManager implements IServerManager
{
    private final Logger logger = LoggerFactory.getLogger(LobbyManager.class);
    @Inject
    private LobbyHubsManager lobbyHubsManager;
    @Inject
    private BukkitApiCore    apiCore;

    @Override
    public void start()
    {
        this.logger.info("Hub component started successfully");
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

    @Override
    public IPlayerStatus getLocation(final Player player)
    {
        final String hubId = this.getLocalHub().getHubWorld(player).getHubId();
        return new InHubStatus(this.apiCore.getServerId(), hubId);
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
