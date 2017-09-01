package pl.north93.zgame.api.global.network.impl;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerProxyData;
import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.api.global.network.server.ServerType;
import pl.north93.zgame.api.global.network.server.group.IServersGroup;
import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackNullable;
import pl.north93.zgame.api.global.redis.messaging.templates.extra.ServersGroupInStringTemplate;
import pl.north93.zgame.api.global.redis.rpc.IRpcTarget;
import pl.north93.zgame.api.global.redis.rpc.Targets;

/**
 * Obiekt przechowujacy dane o serwerze uruchomionym w sieci.
 * Sluzy do przekazywania danych przez redisa.
 */
public class ServerDto implements Server, ServerProxyData
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

    public ServerDto() // for serialization
    {
    }

    public ServerDto(final UUID serverId, final Boolean isLaunchedViaDaemon, final ServerType serverType, final ServerState serverState, final JoiningPolicy joiningPolicy, final String connectIp, final Integer connectPort)
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

    public ServerDto(final UUID serverId, final Boolean isLaunchedViaDaemon, final ServerType serverType, final ServerState serverState, final JoiningPolicy joiningPolicy, final String connectIp, final Integer connectPort, final IServersGroup serversGroup)
    {
        this(serverId, isLaunchedViaDaemon, serverType, serverState, joiningPolicy, connectIp, connectPort);
        this.serversGroup = serversGroup;
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
}
