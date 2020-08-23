package pl.north93.northplatform.api.minigame.server.gamehost;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.metadata.MetaStore;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArenaManager;
import pl.north93.northplatform.api.minigame.shared.api.IGameHostRpc;
import pl.north93.northplatform.api.minigame.shared.api.PlayerJoinInfo;
import pl.north93.northplatform.api.minigame.shared.api.arena.RemoteArena;
import pl.north93.northplatform.api.minigame.shared.api.arena.reconnect.ReconnectTicket;

@Slf4j
public class GameHostRpcImpl implements IGameHostRpc
{
    private final LocalArenaManager localArenaManager;

    public GameHostRpcImpl(final LocalArenaManager localArenaManager)
    {
        this.localArenaManager = localArenaManager;
    }

    @Override
    public List<RemoteArena> getArenas()
    {
        final List<LocalArena> arenas = this.localArenaManager.getArenas();
        return arenas.stream().map(LocalArena::getAsRemoteArena).collect(Collectors.toList());
    }

    @Override
    public boolean tryConnectPlayers(final List<PlayerJoinInfo> players, final UUID arenaId, final MetaStore metadata)
    {
        final LocalArena arena = this.localArenaManager.getArena(arenaId);
        if (arena == null)
        {
            log.warn("arena is null in tryConnectPlayers()");
            return false;
        }

        return arena.getPlayersManager().tryAddPlayers(players, metadata);
    }

    @Override
    public boolean tryConnectSpectators(final List<PlayerJoinInfo> players, final UUID arenaId)
    {
        final LocalArena arena = this.localArenaManager.getArena(arenaId);
        if (arena == null)
        {
            log.warn("arena is null in tryConnectSpectators()");
            return false;
        }

        return arena.getPlayersManager().tryAddSpectators(players);
    }

    @Override
    public boolean tryReconnect(final ReconnectTicket ticket)
    {
        final LocalArena arena = this.localArenaManager.getArena(ticket.getArenaId());
        if (arena == null)
        {
            log.warn("arena is null in tryReconnect()");
            return false;
        }

        return arena.getPlayersManager().tryReconnect(ticket);
    }
}
