package pl.arieals.api.minigame.server.gamehost.arena;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.arieals.api.minigame.server.shared.api.PlayerJoinInfo;
import pl.arieals.api.minigame.shared.api.MiniGame;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.impl.ArenaManager;

/**
 * Każda LocalArena ma swojego PlayersManagera
 */
public class PlayersManager
{
    private final GameHostManager      gameHostManager;
    private final ArenaManager         manager;
    private final LocalArena           arena;
    private final List<Player>         players; // lista połączonych już graczy
    private final List<PlayerJoinInfo> joinInfos;

    public PlayersManager(final GameHostManager gameHostManager, final ArenaManager manager, final LocalArena arena)
    {
        this.gameHostManager = gameHostManager;
        this.manager = manager;
        this.arena = arena;
        this.players = new ArrayList<>();
        this.joinInfos = new ArrayList<>();
    }

    public List<Player> getPlayers()
    {
        return this.players;
    }

    public boolean tryAddPlayers(final List<PlayerJoinInfo> players, final boolean spectator)
    {
        // todo implement spectating?
        synchronized (this) // handle only one join request at same time
        {
            if (! this.canJoin(players))
            {
                return false;
            }
            this.joinInfos.addAll(players);

            final List<UUID> playerIds = players.stream().map(PlayerJoinInfo::getUuid).collect(Collectors.toList());
            final RemoteArena remoteArena = this.arena.getAsRemoteArena();
            remoteArena.getPlayers().addAll(playerIds);
            this.manager.setArena(remoteArena);

            return true;
        }
    }

    private boolean canJoin(final List<PlayerJoinInfo> players)
    {
        final MiniGame miniGame = this.gameHostManager.getMiniGame();
        final int normalSlots = miniGame.getSlots() - miniGame.getVipSlots();

        final int totalPlayers = this.joinInfos.size();

        final long joiningPlayers = players.size();
        final long joiningVips = players.stream().filter(PlayerJoinInfo::isVip).count();
        final long joiningNormals = joiningPlayers - joiningVips;

        return joiningPlayers + totalPlayers <= normalSlots || joiningNormals + totalPlayers <= normalSlots && joiningPlayers + totalPlayers <= miniGame.getSlots();
    }

    public void playerConnected(final Player player)
    {
        this.players.add(player);
        this.gameHostManager.getLobbyManager().addPlayer(this.arena, player);
        Bukkit.getPluginManager().callEvent(new PlayerJoinArenaEvent(player, this.arena));
        Bukkit.broadcastMessage("playerConnected(" + player.getName() + ") arena " + this.arena.getId()); // debug message
    }

    public void playerDisconnected(final Player player)
    {
        this.players.remove(player);
        this.joinInfos.removeIf(pji -> pji.getUuid().equals(player.getUniqueId()));

        final RemoteArena remoteArena = this.arena.getAsRemoteArena();
        remoteArena.getPlayers().remove(player.getUniqueId());
        this.manager.setArena(remoteArena);

        Bukkit.getPluginManager().callEvent(new PlayerQuitArenaEvent(player, this.arena));
    }
}
