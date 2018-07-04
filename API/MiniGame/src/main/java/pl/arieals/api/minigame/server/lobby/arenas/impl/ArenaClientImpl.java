package pl.arieals.api.minigame.server.lobby.arenas.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import pl.arieals.api.minigame.shared.impl.arena.ArenaManager;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
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
        final Set<RemoteArena> allArenas = this.arenaManager.getAllArenas();

        this.arenas = new HashMap<>(allArenas.size());
        for (final RemoteArena arena : allArenas)
        {
            this.arenas.put(arena.getId(), arena);
        }

        this.observers = new ConcurrentHashMap<>();
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
    public IArena get(final UUID arenaId)
    {
        return this.arenas.get(arenaId);
    }

    @Override
    public Collection<IArena> get(final ArenaQuery query)
    {
        final Collection<IArena> arenas = new ArrayList<>(this.arenas.values());
        final Comparator<IArena> comparator = Comparator.comparing(IArena::getPlayersCount).reversed();
        return arenas.stream().filter(query).sorted(comparator).collect(Collectors.toList());
    }
    
    @Override
    public Collection<IArena> getAll()
    {
        return new ArrayList<>(arenas.values());
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
        return this.doConnect(arena, players, false);
    }

    @Override
    public boolean spectate(final IArena arena, final PlayerJoinInfo playerJoinInfo)
    {
        return this.doConnect(arena, Collections.singletonList(playerJoinInfo), true);
    }

    private boolean doConnect(final IArena arena, final Collection<PlayerJoinInfo> players, final boolean spectator)
    {
        final IGameHostRpc rpcProxy = this.getGameHostRpc(arena.getServerId());

        try
        {
            if (! rpcProxy.tryConnectPlayers(new ArrayList<>(players), arena.getId(), spectator))
            {
                return false;
            }
        }
        catch (final RpcException exception)
        {
            exception.printStackTrace();
            return false;
        }

        final Server server = this.networkManager.getServers().withUuid(arena.getServerId());
        for (final PlayerJoinInfo player : players)
        {
            final IPlayersManager.Unsafe unsafe = this.networkManager.getPlayers().unsafe();
            unsafe.getOnlineValue(player.getUuid()).ifPresent(playerValue -> playerValue.get().connectTo(server));
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
        if (arena == null)
        {
            return;
        }

        this.arenas.put(arenaId, arena);
        this.fireObservers(arena, observer -> observer.arenaCreated(arena));
    }

    @NetEventSubscriber(ArenaDataChangedNetEvent.class)
    private synchronized void onArenaUpdate(final ArenaDataChangedNetEvent event)
    {
        final UUID arenaId = event.getArenaId();

        final RemoteArena arena = this.arenaManager.getArena(arenaId);
        if (arena == null)
        {
            this.doRemoveArena(arenaId);
            return;
        }

        this.arenas.put(arenaId, arena);
        this.fireObservers(arena, observer -> observer.arenaUpdated(arena));
    }

    @NetEventSubscriber(ArenaDeletedNetEvent.class)
    private synchronized void onArenaDelete(final ArenaDeletedNetEvent event)
    {
        this.doRemoveArena(event.getArenaId());
    }

    private synchronized void doRemoveArena(final UUID arenaId)
    {
        final IArena arena = this.arenas.remove(arenaId);
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
