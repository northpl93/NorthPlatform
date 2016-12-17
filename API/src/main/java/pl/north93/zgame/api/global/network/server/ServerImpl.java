package pl.north93.zgame.api.global.network.server;

import static pl.north93.zgame.api.global.redis.RedisKeys.SERVER;


import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.deployment.ServerPattern;
import pl.north93.zgame.api.global.deployment.ServersGroup;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.redis.messaging.RedisUpdatable;
import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackNullable;
import pl.north93.zgame.api.global.redis.messaging.templates.extra.ServerPatternInStringTemplate;
import pl.north93.zgame.api.global.redis.messaging.templates.extra.ServersGroupInStringTemplate;

public class ServerImpl implements Server, ServerProxyData, RedisUpdatable
{
    private UUID          serverId;
    private Boolean       isLaunchedViaDaemon;
    private ServerType    serverType;
    private ServerState   serverState;
    private JoiningPolicy joiningPolicy;
    @MsgPackNullable
    @MsgPackCustomTemplate(ServersGroupInStringTemplate.class)
    private ServersGroup  serversGroup;
    @MsgPackNullable
    @MsgPackCustomTemplate(ServerPatternInStringTemplate.class)
    private ServerPattern serverPattern;

    public ServerImpl() // for serialization
    {
    }

    public ServerImpl(final UUID serverId, final Boolean isLaunchedViaDaemon, final ServerType serverType, final ServerState serverState, final JoiningPolicy joiningPolicy)
    {
        this.serverId = serverId;
        this.isLaunchedViaDaemon = isLaunchedViaDaemon;
        this.serverType = serverType;
        this.serverState = serverState;
        this.joiningPolicy = joiningPolicy;
    }

    public ServerImpl(final UUID serverId, final Boolean isLaunchedViaDaemon, final ServerType serverType, final ServerState serverState, final JoiningPolicy joiningPolicy, final ServersGroup serversGroup, final ServerPattern serverPattern)
    {
        this(serverId, isLaunchedViaDaemon, serverType, serverState, joiningPolicy);
        this.serversGroup = serversGroup;
        this.serverPattern = serverPattern;
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
    public ServerPattern getServerPattern()
    {
        return this.serverPattern;
    }

    @Override
    public Optional<ServersGroup> getServersGroup()
    {
        return Optional.ofNullable(this.serversGroup);
    }

    @Override
    public String getProxyName()
    {
        return String.valueOf(this.serverId);
    }

    @Override
    public String getConnectHost()
    {
        return null;
    }

    @Override
    public int getConnectPort()
    {
        return 0;
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

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder // ServerImpl builder
    {
        // TODO
    }
}
