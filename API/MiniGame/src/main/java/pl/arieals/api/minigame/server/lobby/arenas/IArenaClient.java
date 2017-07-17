package pl.arieals.api.minigame.server.lobby.arenas;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import pl.arieals.api.minigame.shared.api.PlayerJoinInfo;
import pl.arieals.api.minigame.shared.api.IGameHostRpc;
import pl.arieals.api.minigame.shared.api.arena.IArena;

public interface IArenaClient
{
    Collection<IArena> get(ArenaQuery query);

    void observe(ArenaQuery query, IArenaObserver observer);

    boolean connect(ArenaQuery query, Collection<PlayerJoinInfo> players);

    boolean connect(IArena arena, Collection<PlayerJoinInfo> players);

    default boolean connect(final IArena arena, final PlayerJoinInfo playerJoinInfo)
    {
        return this.connect(arena, Collections.singleton(playerJoinInfo));
    }

    IGameHostRpc getGameHostRpc(UUID serverId);
}
