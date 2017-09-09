package pl.north93.zgame.api.global.network.impl;

import static pl.north93.zgame.api.global.utils.CollectionUtils.findInCollection;


import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.server.IServerRpc;
import pl.north93.zgame.api.global.network.server.IServersManager;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.group.IServersGroup;
import pl.north93.zgame.api.global.network.server.group.ServersGroupDto;
import pl.north93.zgame.api.global.redis.observable.Hash;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;

class ServersManagerImpl implements IServersManager
{
    private final Unsafe          unsafe = new ServersManagerUnsafe();
    private Hash<ServerDto>       servers;
    private Hash<ServersGroupDto> serversGroups;

    public ServersManagerImpl(final IObservationManager observationManager)
    {
        this.servers = observationManager.getHash(ServerDto.class, "servers");
        this.serversGroups = observationManager.getHash(ServersGroupDto.class, "servers_groups");
    }

    @Override
    public Server withUuid(final UUID uuid)
    {
        return this.servers.get(uuid.toString());
    }

    @Override
    public IServerRpc getServerRpc(final UUID uuid)
    {
        throw new UnsupportedOperationException(); // todo
    }

    @Override
    public Set<? extends Server> all()
    {
        return this.servers.values();
    }

    @Override
    public Set<Server> inGroup(final String group)
    {
        return this.all().stream().filter(server -> server.getServersGroup().getName().equals(group)).collect(Collectors.toSet());
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
