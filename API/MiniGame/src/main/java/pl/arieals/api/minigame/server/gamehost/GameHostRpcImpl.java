package pl.arieals.api.minigame.server.gamehost;

import java.util.List;
import java.util.UUID;

import pl.arieals.api.minigame.shared.api.IGameHostRpc;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;

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
        return null;
    }

    @Override
    public Boolean tryConnectPlayer(final UUID playerId, final UUID arenaId)
    {
        return null;
    }
}
