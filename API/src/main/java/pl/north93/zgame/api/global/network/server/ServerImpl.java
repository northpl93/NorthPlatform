package pl.north93.zgame.api.global.network.server;

import static pl.north93.zgame.api.global.redis.RedisKeys.SERVER;


import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.deployment.ServersGroup;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.redis.messaging.RedisUpdatable;

public class ServerImpl implements Server, RedisUpdatable
{
    private UUID          serverId;
    private Boolean       isLaunchedViaDaemon;
    private ServerType    serverType;
    private ServerState   serverState;
    private JoiningPolicy joiningPolicy;
    private String        serversGroup;

    public ServerImpl() // for serialization
    {
    }

    public ServerImpl(final UUID serverId, final Boolean isLaunchedViaDaemon, final ServerType serverType, final ServerState serverState, final JoiningPolicy joiningPolicy, final String serversGroup)
    {
        this.serverId = serverId;
        this.isLaunchedViaDaemon = isLaunchedViaDaemon;
        this.serverType = serverType;
        this.serverState = serverState;
        this.joiningPolicy = joiningPolicy;
        this.serversGroup = serversGroup;
    }

    @Override
    public String getRedisKey()
    {
        return SERVER + this.serverId;
    }

    @Override
    public UUID getUuid()
    {
        return this.serverId;
    }

    @Override
    public ServerType getType()
    {
        return this.serverType;
    }

    @Override
    public boolean isLaunchedViaDaemon()
    {
        return this.isLaunchedViaDaemon;
    }

    @Override
    public ServerState getServerState()
    {
        return this.serverState;
    }

    @Override
    public JoiningPolicy getJoiningPolicy()
    {
        return this.joiningPolicy;
    }

    @Override
    public Optional<ServersGroup> getServersGroup()
    {
        return Optional.ofNullable(API.getNetworkManager().getServersGroup(this.serversGroup));
    }

    public void updateServerState(final ServerState serverState)
    {
        this.serverState = serverState;
        this.sendUpdate();
        API.getLogger().info("Server with ID " + this.serverId + " is now in state " + serverState);
    }

    public void updateJoiningPolicy(final JoiningPolicy joiningPolicy)
    {
        this.joiningPolicy = joiningPolicy;
        this.sendUpdate();
        API.getLogger().info("Server with ID " + this.serverId + " has now joining policy " + joiningPolicy);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serverId", this.serverId).append("isLaunchedViaDaemon", this.isLaunchedViaDaemon).append("serverType", this.serverType).append("serverState", this.serverState).toString();
    }
}
