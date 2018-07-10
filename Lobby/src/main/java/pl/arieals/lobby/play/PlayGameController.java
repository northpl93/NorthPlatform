package pl.arieals.lobby.play;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.arieals.api.minigame.server.lobby.arenas.ArenaQuery;
import pl.arieals.api.minigame.server.lobby.arenas.IArenaClient;
import pl.arieals.api.minigame.server.lobby.hub.HubWorld;
import pl.arieals.api.minigame.server.lobby.hub.LocalHubServer;
import pl.arieals.api.minigame.server.utils.party.PartyClient;
import pl.arieals.api.minigame.shared.api.GameIdentity;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.PlayerJoinInfo;
import pl.arieals.api.minigame.shared.api.arena.IArena;
import pl.arieals.api.minigame.shared.api.hub.IHubServer;
import pl.arieals.api.minigame.shared.api.party.IParty;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

/**
 * Kontroler obsługujący całą logikę dołączenia gracza do gry.
 * Tutaj trafiają rządania prosto od gracza (np z kliknięcia w gui).
 */
public class PlayGameController
{
    @Inject
    private Logger         logger;
    @Inject
    private MiniGameServer miniGameServer;
    @Inject
    private IArenaClient   arenaClient;
    @Inject
    private PartyClient    partyClient;

    @Bean
    private PlayGameController()
    {
    }

    public void switchHub(final Player player, final String hubId)
    {
        if (this.partyClient.cantDecideAboutHimself(player))
        {
            // gracz jest w grupie i nie jest liderem więc nie może decydować gdzie gra
            return;
        }

        final LobbyManager lobby = this.miniGameServer.getServerManager();
        lobby.getLocalHub().movePlayerToHub(player, hubId);
    }

    public void switchHubInstance(final Player player, final IHubServer hubServer)
    {
        if (this.partyClient.cantDecideAboutHimself(player))
        {
            // gracz jest w grupie i nie jest liderem więc nie może decydować gdzie gra
            return;
        }

        final LobbyManager lobby = this.miniGameServer.getServerManager();
        final HubWorld hubWorld = lobby.getLocalHub().getHubWorld(player);

        lobby.tpToHub(Collections.singletonList(player), hubServer, hubWorld.getHubId());
    }

    public Collection<? extends IHubServer> getHubs()
    {
        final LobbyManager lobby = this.miniGameServer.getServerManager();
        return lobby.getAllHubServers();
    }

    public LocalHubServer getThisHubServer()
    {
        final LobbyManager lobby = this.miniGameServer.getServerManager();
        return lobby.getLocalHub();
    }
    
    public void playGame(final Player player, final IArena arena)
    {
        if (this.partyClient.cantDecideAboutHimself(player))
        {
            return;
        }

        final Collection<PlayerJoinInfo> players = this.getPlayerTeamJoinInfos(player);
        this.arenaClient.connect(arena, players);
    }

    public void playGame(final Player player, final GameIdentity gameIdentity, final boolean allowInProgress, final String worldId)
    {
        if (this.partyClient.cantDecideAboutHimself(player))
        {
            // gracz jest w grupie i nie jest liderem więc nie może decydować gdzie gra
            return;
        }

        final ArenaQuery query = ArenaQuery.create().miniGame(gameIdentity).gamePhase(GamePhase.LOBBY).world(worldId);
        if (allowInProgress)
        {
            query.gamePhase(GamePhase.STARTED);
        }

        final Collection<PlayerJoinInfo> players = this.getPlayerTeamJoinInfos(player);
        this.arenaClient.connect(query, players);
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
