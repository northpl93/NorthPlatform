package pl.north93.zgame.api.global.network.impl;

import static pl.north93.zgame.api.global.redis.RedisKeys.SERVER;


import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.deployment.ServerPattern;
import pl.north93.zgame.api.global.deployment.serversgroup.IServersGroup;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerProxyData;
import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.api.global.network.server.ServerType;
import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackNullable;
import pl.north93.zgame.api.global.redis.messaging.templates.extra.ServerPatternInStringTemplate;
import pl.north93.zgame.api.global.redis.messaging.templates.extra.ServersGroupInStringTemplate;
import pl.north93.zgame.api.global.redis.observable.ObjectKey;
import pl.north93.zgame.api.global.redis.rpc.IRpcTarget;
import pl.north93.zgame.api.global.redis.rpc.Targets;

public class ServerImpl implements Server, ServerProxyData
{
    private UUID          serverId;
    private String        connectIp;
    private Integer       connectPort;
    private Boolean       isLaunchedViaDaemon;
    private ServerType    serverType;
    private ServerState   serverState;
    private Boolean       shutdown;
    private JoiningPolicy joiningPolicy;
    @MsgPackNullable
    @MsgPackCustomTemplate(ServersGroupInStringTemplate.class)
    private IServersGroup serversGroup;
    @MsgPackNullable
    @MsgPackCustomTemplate(ServerPatternInStringTemplate.class)
    private ServerPattern serverPattern;

    public ServerImpl() // for serialization
    {
    }

    public ServerImpl(final UUID serverId, final Boolean isLaunchedViaDaemon, final ServerType serverType, final ServerState serverState, final JoiningPolicy joiningPolicy, final String connectIp, final Integer connectPort)
    {
        this.serverId = serverId;
        this.connectIp = connectIp;
        this.connectPort = connectPort;
        this.isLaunchedViaDaemon = isLaunchedViaDaemon;
        this.serverType = serverType;
        this.serverState = serverState;
        this.joiningPolicy = joiningPolicy;
        this.shutdown = false;
    }

    public ServerImpl(final UUID serverId, final Boolean isLaunchedViaDaemon, final ServerType serverType, final ServerState serverState, final JoiningPolicy joiningPolicy, final String connectIp, final Integer connectPort, final IServersGroup serversGroup, final ServerPattern serverPattern)
    {
        this(serverId, isLaunchedViaDaemon, serverType, serverState, joiningPolicy, connectIp, connectPort);
        this.serversGroup = serversGroup;
        this.serverPattern = serverPattern;
    }

    @Override
    public ObjectKey getKey()
    {
        return new ObjectKey(SERVER + this.serverId);
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
    public boolean isShutdownScheduled()
    {
        return this.shutdown;
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
    public Optional<IServersGroup> getServersGroup()
    {
        return Optional.ofNullable(this.serversGroup);
    }

    @Override
    public IRpcTarget getRpcTarget()
    {
        return Targets.server(this.serverId);
    }

    @Override
    public String getProxyName()
    {
        return String.valueOf(this.serverId);
    }

    @Override
    public String getConnectHost()
    {
        return this.connectIp;
    }

    @Override
    public int getConnectPort()
    {
        return this.connectPort;
    }

    public void setServerState(final ServerState serverState)
    {
        this.serverState = serverState;
    }

    public void setShutdownScheduled(final Boolean shutdown)
    {
        this.shutdown = shutdown;
    }

    public void setJoiningPolicy(final JoiningPolicy joiningPolicy)
    {
        this.joiningPolicy = joiningPolicy;
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
