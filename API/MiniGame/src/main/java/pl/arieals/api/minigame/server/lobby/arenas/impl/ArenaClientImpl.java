package pl.arieals.api.minigame.server.lobby.arenas.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.lobby.arenas.ArenaQuery;
import pl.arieals.api.minigame.server.lobby.arenas.IArenaClient;
import pl.arieals.api.minigame.server.lobby.arenas.IArenaObserver;
import pl.arieals.api.minigame.shared.api.IGameHostRpc;
import pl.arieals.api.minigame.shared.api.PlayerJoinInfo;
import pl.arieals.api.minigame.shared.api.arena.IArena;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.api.arena.netevent.ArenaCreatedNetEvent;
import pl.arieals.api.minigame.shared.api.arena.netevent.ArenaDataChangedNetEvent;
import pl.arieals.api.minigame.shared.api.arena.netevent.ArenaDeletedNetEvent;
import pl.arieals.api.minigame.shared.impl.ArenaManager;
import pl.north93.zgame.api.global.component.annotations.PostInject;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.redis.event.NetEventSubscriber;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.Targets;
import pl.north93.zgame.api.global.redis.rpc.exceptions.RpcException;

public class ArenaClientImpl implements IArenaClient
{
    private Map<UUID, IArena>               arenas;
    private Map<ArenaQuery, IArenaObserver> observers;
    @Inject
    private ArenaManager      arenaManager;
    @Inject
    private IRpcManager       rpcManager;
    @Inject
    private INetworkManager   networkManager;

    @Bean
    private ArenaClientImpl()
    {
        this.arenas = new HashMap<>(100);
        this.observers = new ConcurrentHashMap<>();
    }

    @PostInject
    private synchronized void postInject()
    {
        final Set<RemoteArena> allArenas = this.arenaManager.getAllArenas();
        for (final RemoteArena arena : allArenas)
        {
            this.arenas.put(arena.getId(), arena);
        }
    }

    private void fireObservers(final IArena arena, final Consumer<IArenaObserver> action)
    {
        for (final Map.Entry<ArenaQuery, IArenaObserver> observerEntry : this.observers.entrySet())
        {
            final ArenaQuery observerQuery = observerEntry.getKey();
            if (! observerQuery.test(arena))
            {
                continue;
            }

            action.accept(observerEntry.getValue());
        }
    }

    @Override
    public Collection<IArena> get(final ArenaQuery query)
    {
        final Collection<IArena> arenas = new ArrayList<>(this.arenas.values());
        return arenas.stream().filter(query).sorted(Comparator.comparing(IArena::getPlayersCount)).collect(Collectors.toList());
    }

    @Override
    public synchronized void observe(final ArenaQuery query, final IArenaObserver observer)
    {
        this.observers.put(query, observer);
    }

    @Override
    public boolean connect(final ArenaQuery query, final Collection<PlayerJoinInfo> players)
    {
        final Collection<IArena> results = this.get(query);
        for (final IArena result : results)
        {
            if (this.connect(result, players))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean connect(final IArena arena, final Collection<PlayerJoinInfo> players)
    {
        final IGameHostRpc rpcProxy = this.getGameHostRpc(arena.getServerId());

        Boolean connectionResult;
        try
        {
            connectionResult = rpcProxy.tryConnectPlayers(new ArrayList<>(players), arena.getId(), false);
        }
        catch (final RpcException exception)
        {
            exception.printStackTrace();
            connectionResult = false;
        }

        if (! connectionResult)
        {
            return false;
        }

        final Server server = this.networkManager.getServers().withUuid(arena.getServerId());
        for (final PlayerJoinInfo player : players)
        {
            this.networkManager.getPlayers().unsafe().getOnline(player.getUuid()).get().connectTo(server);
        }
        return true;
    }

    @Override
    public IGameHostRpc getGameHostRpc(final UUID serverId)
    {
        return this.rpcManager.createRpcProxy(IGameHostRpc.class, Targets.server(serverId));
    }

    @NetEventSubscriber(ArenaCreatedNetEvent.class)
    private synchronized void onArenaCreate(final ArenaCreatedNetEvent event)
    {
        final UUID arenaId = event.getArenaId();
        final RemoteArena arena = this.arenaManager.getArena(arenaId);

        this.arenas.put(arenaId, arena);

        this.fireObservers(arena, observer -> observer.arenaCreated(arena));
    }

    @NetEventSubscriber(ArenaDataChangedNetEvent.class)
    private synchronized void onArenaUpdate(final ArenaDataChangedNetEvent event)
    {
        final UUID arenaId = event.getArenaId();
        final RemoteArena arena = this.arenaManager.getArena(arenaId);

        this.arenas.put(arenaId, arena);

        this.fireObservers(arena, observer -> observer.arenaUpdated(arena));
    }

    @NetEventSubscriber(ArenaDeletedNetEvent.class)
    private synchronized void onArenaDelete(final ArenaDeletedNetEvent event)
    {
        final IArena arena = this.arenas.remove(event.getArenaId());
        if (arena != null)
        {
            this.fireObservers(arena, observer -> observer.arenaRemoved(arena));
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arenas", this.arenas).toString();
    }
}
