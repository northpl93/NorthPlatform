package pl.arieals.api.minigame.server.gamehost;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.IGameHostRpc;
import pl.arieals.api.minigame.shared.api.PlayerJoinInfo;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.api.arena.reconnect.ReconnectTicket;
import pl.north93.zgame.api.global.metadata.MetaStore;

@Slf4j
public class GameHostRpcImpl implements IGameHostRpc
{
    private final GameHostManager manager;

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
    public Boolean tryConnectPlayers(final List<PlayerJoinInfo> players, final UUID arenaId, final MetaStore metadata)
    {
        final LocalArena arena = this.manager.getArenaManager().getArena(arenaId);
        if (arena == null)
        {
            log.warn("arena is null in tryConnectPlayers()");
            return false;
        }

        return arena.getPlayersManager().tryAddPlayers(players, metadata);
    }

    @Override
    public Boolean tryConnectSpectators(final List<PlayerJoinInfo> players, final UUID arenaId)
    {
        final LocalArena arena = this.manager.getArenaManager().getArena(arenaId);
        if (arena == null)
        {
            log.warn("arena is null in tryConnectSpectators()");
            return false;
        }

        return arena.getPlayersManager().tryAddSpectators(players);
    }

    @Override
    public Boolean tryReconnect(final ReconnectTicket ticket)
    {
        final LocalArena arena = this.manager.getArenaManager().getArena(ticket.getArenaId());
        if (arena == null)
        {
            log.warn("arena is null in tryReconnect()");
            return false;
        }

        return arena.getPlayersManager().tryReconnect(ticket);
    }
}
