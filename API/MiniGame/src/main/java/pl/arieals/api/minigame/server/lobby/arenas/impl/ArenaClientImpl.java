package pl.arieals.api.minigame.server.lobby.arenas.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import pl.arieals.api.minigame.server.lobby.arenas.ArenaQuery;
import pl.arieals.api.minigame.server.lobby.arenas.IArenaClient;
import pl.arieals.api.minigame.server.lobby.arenas.IArenaObserver;
import pl.arieals.api.minigame.shared.api.PlayerJoinInfo;
import pl.arieals.api.minigame.shared.api.IGameHostRpc;
import pl.arieals.api.minigame.shared.api.arena.IArena;
import pl.arieals.api.minigame.shared.api.arena.netevent.ArenaCreatedNetEvent;
import pl.arieals.api.minigame.shared.api.arena.netevent.ArenaDataChangedNetEvent;
import pl.arieals.api.minigame.shared.impl.ArenaManager;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.redis.event.NetEventSubscriber;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.Targets;

public class ArenaClientImpl implements IArenaClient
{
    @Inject
    private ArenaManager    arenaManager;
    @Inject
    private IRpcManager     rpcManager;
    @Inject
    private INetworkManager networkManager;

    @Bean
    private ArenaClientImpl()
    {
    }

    @Override
    public Collection<IArena> get(final ArenaQuery query)
    {
        return null;
    }

    @Override
    public void observe(final ArenaQuery query, final IArenaObserver observer)
    {

    }

    @Override
    public boolean connect(final ArenaQuery query, final Collection<PlayerJoinInfo> players)
    {
        return false;
    }

    @Override
    public boolean connect(final IArena arena, final Collection<PlayerJoinInfo> players)
    {
        final IGameHostRpc rpcProxy = this.getGameHostRpc(arena.getServerId());

        if (! rpcProxy.tryConnectPlayers(new ArrayList<>(players), arena.getId(), false))
        {
            return false;
        }

        final Server server = this.networkManager.getServers().withUuid(arena.getServerId());
        for (final PlayerJoinInfo player : players)
        {
            this.networkManager.getOnlinePlayer(player.getUuid()).get().connectTo(server);
        }
        return true;
    }

    @Override
    public IGameHostRpc getGameHostRpc(final UUID serverId)
    {
        return this.rpcManager.createRpcProxy(IGameHostRpc.class, Targets.server(serverId));
    }

    @NetEventSubscriber(ArenaCreatedNetEvent.class)
    private void onArenaCreate(final ArenaCreatedNetEvent event)
    {
        System.out.println("Arena created: " + event);
    }

    @NetEventSubscriber(ArenaDataChangedNetEvent.class)
    public void onArenaUpdate(final ArenaDataChangedNetEvent event)
    {
        System.out.println("Arena data changed: " + event);
    }
}
