package pl.north93.zgame.api.global.network.impl.servers;

import static pl.north93.zgame.api.global.utils.lang.CollectionUtils.findInCollection;


import java.util.Comparator;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.network.server.IServerRpc;
import pl.north93.zgame.api.global.network.server.IServersManager;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.api.global.network.server.group.IServersGroup;
import pl.north93.zgame.api.global.network.server.group.ServersGroupDto;
import pl.north93.zgame.api.global.redis.observable.Hash;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.Targets;

/*default*/ class ServersManagerImpl implements IServersManager
{
    private final Unsafe          unsafe = new ServersManagerUnsafe();
    private final IRpcManager     rpcManager;
    private Hash<ServerDto>       servers;
    private Hash<ServersGroupDto> serversGroups;

    @Bean
    private ServersManagerImpl(final IRpcManager rpcManager, final IObservationManager observationManager)
    {
        this.rpcManager = rpcManager;
        this.servers = observationManager.getHash(ServerDto.class, "servers");
        this.serversGroups = observationManager.getHash(ServersGroupDto.class, "servers_groups");
    }

    @Override
    public Server withUuid(final UUID uuid)
    {
        return this.servers.get(uuid.toString());
    }

    @Override
    public Server getLeastLoadedServerInGroup(final String group)
    {
        final Comparator<Server> playersComparator = Comparator.comparing(Server::getPlayersCount);

        return this.inGroupStream(group)
                   .filter(this::checkIsServerWorking)
                   .min(playersComparator)
                   .orElse(null);
    }

    private boolean checkIsServerWorking(final Server server)
    {
        // nie chcemy teleportowac graczy na serwery wlaczajace/wylaczajace sie i
        // zaplanowane do wylaczenia
        return server.getServerState() == ServerState.WORKING && ! server.isShutdownScheduled();
    }

    @Override
    public IServerRpc getServerRpc(final UUID uuid)
    {
        return this.rpcManager.createRpcProxy(IServerRpc.class, Targets.server(uuid));
    }

    @Override
    public Set<? extends Server> all()
    {
        return this.servers.values();
    }

    @Override
    public Set<Server> inGroup(final String group)
    {
        return this.inGroupStream(group).collect(Collectors.toSet());
    }

    private Stream<? extends Server> inGroupStream(final String group)
    {
        return this.all().stream().filter(server -> server.getServersGroup().getName().equals(group));
    }

    @Override
    public Set<? extends IServersGroup> getServersGroups()
    {
        return this.serversGroups.values();
    }

    @Override
    public IServersGroup getServersGroup(final String name)
    {
        return findInCollection(this.getServersGroups(), IServersGroup::getName, name);
    }

    @Override
    public Unsafe unsafe()
    {
        return this.unsafe;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("servers", this.servers).append("serversGroups", this.serversGroups).toString();
    }

    private final class ServersManagerUnsafe implements Unsafe
    {
        @Override
        public Value<ServerDto> getServerDto(final UUID serverId)
        {
            return ServersManagerImpl.this.servers.getAsValue(serverId.toString());
        }

        @Override
        public Hash<ServersGroupDto> getServersGroups()
        {
            return ServersManagerImpl.this.serversGroups;
        }
    }
}
