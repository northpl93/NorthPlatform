package pl.north93.northplatform.lobby.play;

import java.util.Collection;
import java.util.Collections;

import org.bukkit.entity.Player;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.server.lobby.LobbyManager;
import pl.north93.northplatform.api.minigame.server.lobby.arenas.ArenaQuery;
import pl.north93.northplatform.api.minigame.server.lobby.arenas.IArenaClient;
import pl.north93.northplatform.api.minigame.server.lobby.hub.HubWorld;
import pl.north93.northplatform.api.minigame.server.lobby.hub.LocalHubServer;
import pl.north93.northplatform.api.minigame.server.shared.party.PartyClient;
import pl.north93.northplatform.api.minigame.shared.api.GameIdentity;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.north93.northplatform.api.minigame.shared.api.PlayerJoinInfo;
import pl.north93.northplatform.api.minigame.shared.api.arena.IArena;
import pl.north93.northplatform.api.minigame.shared.api.hub.IHubServer;
import pl.north93.northplatform.api.minigame.shared.api.party.IParty;

/**
 * A controller class that handles minigame-related incoming requests from players.
 * (e.g. from GUIs, commands, etc.)
 */
@Slf4j
public class PlayGameController
{
    @Inject
    private LobbyManager lobbyManager;
    @Inject
    private IArenaClient arenaClient;
    @Inject
    private PartyClient partyClient;

    @Bean
    private PlayGameController()
    {
    }

    public void switchHub(final Player player, final String hubId)
    {
        if (this.partyClient.cantDecideAboutHimself(player))
        {
            // player is in a party and isn't a leader, so he can't decide about himself
            log.info("Player {} cant switch hub type because he isnt a party owner", player.getName());
            return;
        }

        this.lobbyManager.getLocalHub().movePlayerToHub(player, hubId);
    }

    public void switchHubInstance(final Player player, final IHubServer hubServer)
    {
        if (this.partyClient.cantDecideAboutHimself(player))
        {
            // player is in a party and isn't a leader, so he can't decide about himself
            log.info("Player {} cant switch hub instance because he isnt a party owner", player.getName());
            return;
        }

        final HubWorld hubWorld = this.lobbyManager.getLocalHub().getHubWorld(player);
        this.lobbyManager.tpToHub(Collections.singletonList(player), hubServer, hubWorld.getHubId());
    }

    public Collection<? extends IHubServer> getHubs()
    {
        return this.lobbyManager.getAllHubServers();
    }

    public LocalHubServer getThisHubServer()
    {
        return this.lobbyManager.getLocalHub();
    }
    
    public void playGame(final Player player, final IArena arena)
    {
        if (this.partyClient.cantDecideAboutHimself(player))
        {
            return;
        }

        final Collection<PlayerJoinInfo> players = this.getPlayerTeamJoinInfos(player);
        if (! this.arenaClient.connect(arena, players))
        {
            final GameIdentity miniGame = arena.getMiniGame();
            log.info("Player {} cant play game {} because arena client denied join request", player.getName(), miniGame);
        }
    }

    public void playGame(final Player player, final GameIdentity gameIdentity, final boolean allowInProgress, final String worldId)
    {
        if (this.partyClient.cantDecideAboutHimself(player))
        {
            // player is in a party and isn't a leader, so he can't decide about himself
            log.info("Player {} cant play game {} because he isnt a party owner", player.getName(), gameIdentity);
            return;
        }

        final ArenaQuery query = ArenaQuery.create().miniGame(gameIdentity).gamePhase(GamePhase.LOBBY).world(worldId);
        if (allowInProgress)
        {
            query.gamePhase(GamePhase.STARTED);
        }

        final Collection<PlayerJoinInfo> players = this.getPlayerTeamJoinInfos(player);
        if (! this.arenaClient.connect(query, players))
        {
            log.info("Player {} cant play game {} because arena client denied join request", player.getName(), gameIdentity);
        }
    }

    private Collection<PlayerJoinInfo> getPlayerTeamJoinInfos(final Player player)
    {
        final IParty party = this.partyClient.getPlayerParty(player);
        if (party == null)
        {
            final boolean isVip = player.hasPermission("gamejoin.vip");
            final PlayerJoinInfo joinInfo = new PlayerJoinInfo(player.getUniqueId(), isVip, false);

            return Collections.singletonList(joinInfo);
        }

        return party.getJoinInfos();
    }
}
