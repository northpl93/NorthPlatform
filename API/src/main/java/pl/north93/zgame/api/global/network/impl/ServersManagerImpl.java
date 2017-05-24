package pl.north93.zgame.api.global.network.impl;

import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_PATTERNS;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_SERVER_GROUPS;
import static pl.north93.zgame.api.global.redis.RedisKeys.SERVER;


import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.lambdaworks.redis.api.sync.RedisCommands;

import pl.north93.zgame.api.global.data.StorageConnector;
import pl.north93.zgame.api.global.deployment.ServerPattern;
import pl.north93.zgame.api.global.deployment.serversgroup.IServersGroup;
import pl.north93.zgame.api.global.network.server.IServerRpc;
import pl.north93.zgame.api.global.network.server.IServersManager;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;

class ServersManagerImpl implements IServersManager
{
    private StorageConnector    storage;
    private NetworkManager      networkManager;
    private TemplateManager     msgPack;
    private IObservationManager observationManager;

    public ServersManagerImpl(final StorageConnector storage, final NetworkManager networkManager, final TemplateManager msgPack, final IObservationManager observationManager)
    {
        this.storage = storage;
        this.networkManager = networkManager;
        this.msgPack = msgPack;
        this.observationManager = observationManager;
    }

    @Override
    public Server withUuid(final UUID uuid)
    {
        //noinspection unchecked
        return this.observationManager.get(ServerImpl.class, SERVER + uuid).get();
    }

    @Override
    public IServerRpc getServerRpc(final UUID uuid)
    {
        return null; // todo
    }

    @Override
    public Set<Server> all()
    {
        try (final RedisCommands<String, byte[]> redis = this.storage.getRedis())
        {
            return redis.keys(SERVER + "*").stream().map(id -> this.msgPack.deserialize(ServerImpl.class, redis.get(id))).collect(Collectors.toSet());
        }
    }

    @Override
    public Set<Server> inGroup(final String group)
    {
        return this.all().stream().filter(server ->
        {
            final Optional<IServersGroup> groupOptional = server.getServersGroup();
            return groupOptional.isPresent() && groupOptional.get().getName().equals(group);
        }).collect(Collectors.toSet());
    }

    @Override
    public Set<IServersGroup> getServersGroups()
    {
        try (final RedisCommands<String, byte[]> redis = this.storage.getRedis())
        {
            return Sets.newCopyOnWriteArraySet(this.msgPack.deserializeList(IServersGroup.class, redis.get(NETWORK_SERVER_GROUPS)));
        }
    }

    @Override
    public IServersGroup getServersGroup(final String name)
    {
        return this.getServersGroups().stream().filter(serversGroup -> serversGroup.getName().equals(name)).findAny().orElse(null);
    }

    @Override
    public List<ServerPattern> getServerPatterns()
    {
        try (final RedisCommands<String, byte[]> redis = this.storage.getRedis())
        {
            return this.msgPack.deserializeList(ServerPattern.class, redis.get(NETWORK_PATTERNS));
        }
    }

    @Override
    public ServerPattern getServerPattern(final String name)
    {
        return this.getServerPatterns().stream().filter(serverPattern -> serverPattern.getPatternName().equals(name)).findAny().orElse(null);
    }
}
