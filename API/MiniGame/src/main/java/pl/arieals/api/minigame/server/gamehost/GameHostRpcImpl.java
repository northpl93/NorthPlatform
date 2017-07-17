package pl.arieals.api.minigame.server.gamehost;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.PlayerJoinInfo;
import pl.arieals.api.minigame.shared.api.IGameHostRpc;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;

public class GameHostRpcImpl implements IGameHostRpc
{
    private final GameHostManager manager;
    private Logger                logger;

    public GameHostRpcImpl(final GameHostManager manager)
    {
        this.manager = manager;
    }

    @Override
    public List<RemoteArena> getArenas()
    {
        final List<LocalArena> arenas = this.manager.getArenaManager().getArenas();
        return arenas.stream().map(LocalArena::getAsRemoteArena).collect(Collectors.toList());
    }

    @Override
    public Boolean tryConnectPlayers(final List<PlayerJoinInfo> players, final UUID arenaId, final Boolean spectator)
    {
        final LocalArena arena = this.manager.getArenaManager().getArena(arenaId);
        if (arena == null)
        {
            this.logger.warning("arena is null in tryConnectPlayers()");
            return false;
        }

        return arena.getPlayersManager().tryAddPlayers(players, spectator);
    }
}
