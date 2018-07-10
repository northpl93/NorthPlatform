package pl.arieals.api.minigame.server.gamehost;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.math.DioriteRandomUtils;

import pl.arieals.api.minigame.server.lobby.hub.SelectHubServerJoinAction;
import pl.arieals.api.minigame.shared.api.hub.IHubServer;
import pl.arieals.api.minigame.shared.api.hub.RemoteHub;
import pl.arieals.api.minigame.shared.impl.HubsManager;
import pl.north93.zgame.api.bukkit.player.IBukkitPlayers;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.server.Server;

/**
 * Klasa pomocnicza do zadan zwiazanych z hubami (poczekalniami) minigier.
 * Znajdujaca sie po stronie gamehosta.
 */
public class GameHostHubsManager
{
    @Inject
    private Logger          logger;
    @Inject
    private INetworkManager networkManager;
    @Inject
    private IBukkitPlayers  bukkitPlayers;
    @Inject
    private HubsManager     hubsManager;

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
            this.logger.log(Level.WARNING, "Can't tp players to hub; not found any hub");
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
        final Server server = this.networkManager.getServers().withUuid(hubServer.getServerId());
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
