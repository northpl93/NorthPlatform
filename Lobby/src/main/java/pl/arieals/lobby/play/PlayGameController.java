package pl.arieals.lobby.play;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.arieals.api.minigame.server.lobby.arenas.ArenaQuery;
import pl.arieals.api.minigame.server.lobby.arenas.IArenaClient;
import pl.arieals.api.minigame.server.utils.party.PartyClient;
import pl.arieals.api.minigame.shared.api.GameIdentity;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.PlayerJoinInfo;
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
        if (! this.partyClient.canDecideAboutHimself(player))
        {
            // gracz jest w grupie i nie jest liderem więc nie może decydować gdzie gra
            return;
        }

        final LobbyManager lobby = this.miniGameServer.getServerManager();
        lobby.getLocalHub().movePlayerToHub(player, hubId);
    }

    public void playGame(final Player player, final GameIdentity gameIdentity, final boolean allowInProgress, final String worldId)
    {
        if (! this.partyClient.canDecideAboutHimself(player))
        {
            // gracz jest w grupie i nie jest liderem więc nie może decydować gdzie gra
            return;
        }

        final Collection<Player> players;

        final IParty party = this.partyClient.getPlayerParty(player);
        if (party == null)
        {
            players = Collections.singletonList(player);
        }
        else
        {
            players = party.getPlayers().stream().map(Bukkit::getPlayer).collect(Collectors.toList());
        }

        if (this.doConnect(players, gameIdentity, allowInProgress, worldId))
        {
            player.sendMessage("udalo sie znalezc arene");
        }
        else
        {
            player.sendMessage("brak areny spelniajacej kryteria");
        }
    }

    private boolean doConnect(final Collection<Player> players, final GameIdentity gameIdentity, final boolean allowInProgress, final String worldId)
    {
        final ArenaQuery query = ArenaQuery.create().miniGame(gameIdentity).gamePhase(GamePhase.LOBBY).world(worldId);
        if (allowInProgress)
        {
            query.gamePhase(GamePhase.STARTED);
        }

        final List<PlayerJoinInfo> joinInfos = players.stream().map(player ->
        {
            return new PlayerJoinInfo(player.getUniqueId(), false, false);
        }).collect(Collectors.toList());

        return this.arenaClient.connect(query, joinInfos);
    }
}
