package pl.north93.northplatform.api.minigame.server.gamehost;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.math.DioriteRandomUtils;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.player.IBukkitPlayers;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.network.server.IServersManager;
import pl.north93.northplatform.api.global.network.server.Server;
import pl.north93.northplatform.api.minigame.server.lobby.hub.SelectHubServerJoinAction;
import pl.north93.northplatform.api.minigame.shared.api.hub.IHubServer;
import pl.north93.northplatform.api.minigame.shared.api.hub.RemoteHub;
import pl.north93.northplatform.api.minigame.shared.impl.HubsManager;

/**
 * Klasa pomocnicza do zadan zwiazanych z hubami (poczekalniami) minigier.
 * Znajdujaca sie po stronie gamehosta.
 */
@Slf4j
public class GameHostHubsManager
{
    private final IServersManager serversManager;
    private final IBukkitPlayers bukkitPlayers;
    private final HubsManager hubsManager;

    @Bean
    private GameHostHubsManager(final IServersManager serversManager, final IBukkitPlayers bukkitPlayers, final HubsManager hubsManager)
    {
        this.serversManager = serversManager;
        this.bukkitPlayers = bukkitPlayers;
        this.hubsManager = hubsManager;
    }

    /**
     * Teleportuje podanych graczy do huba o podanym ID na losowa instancje.
     *
     * @param players Gracze do teleportacji.
     * @param hubId ID huba na ktorego maja trafic gracze.
     */
    public void tpToHub(final Iterable<? extends Player> players, final String hubId)
    {
        final RemoteHub hub = DioriteRandomUtils.getRandom(this.hubsManager.getAllHubs());
        if (hub == null)
        {
            log.warn("Can't tp players to hub; not found any hub");
            return;
        }

        this.tpToHub(players, hub, hubId);
    }

    /**
     * Teleportuje podanych graczy do huba o podanym ID na wskazaną instancję.
     *
     * @param players Gracze do teleportacji.
     * @param hubServer Instancja serwera z hubami.
     * @param hubId ID huba na ktorego maja trafic gracze.
     */
    public void tpToHub(final Iterable<? extends Player> players, final IHubServer hubServer, final String hubId)
    {
        final Server server = this.serversManager.withUuid(hubServer.getServerId());
        for (final Player player : players)
        {
            final INorthPlayer northPlayer = this.bukkitPlayers.getPlayer(player);
            northPlayer.connectTo(server, new SelectHubServerJoinAction(hubId));
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
