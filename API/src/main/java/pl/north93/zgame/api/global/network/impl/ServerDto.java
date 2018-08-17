package pl.north93.zgame.api.global.network.impl;

import java.util.UUID;

import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerProxyData;
import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.api.global.network.server.ServerType;
import pl.north93.zgame.api.global.network.server.group.IServersGroup;
import pl.north93.zgame.api.global.redis.rpc.IRpcTarget;
import pl.north93.zgame.api.global.redis.rpc.Targets;

/**
 * Obiekt przechowujacy dane o serwerze uruchomionym w sieci.
 * Sluzy do przekazywania danych przez redisa.
 */
@ToString
@NoArgsConstructor // for serialization
public class ServerDto implements Server, ServerProxyData
{
    private UUID          serverId;
    private String        connectIp;
    private Integer       connectPort;
    private Boolean       isLaunchedViaDaemon;
    private ServerType    serverType;
    private Integer       playersCount;
    private ServerState   serverState;
    private Boolean       shutdown;
    private JoiningPolicy joiningPolicy;
    //@MsgPackCustomTemplate(ServersGroupInStringTemplate.class)
    private IServersGroup serversGroup;

    public ServerDto(final UUID serverId, final Boolean isLaunchedViaDaemon, final ServerType serverType, final ServerState serverState, final JoiningPolicy joiningPolicy, final String connectIp, final Integer connectPort, final IServersGroup serversGroup)
    {
        this.serverId = serverId;
        this.connectIp = connectIp;
        this.connectPort = connectPort;
        this.isLaunchedViaDaemon = isLaunchedViaDaemon;
        this.serverType = serverType;
        this.playersCount = 0;
        this.serverState = serverState;
        this.joiningPolicy = joiningPolicy;
        this.serversGroup = serversGroup;
        this.shutdown = false;
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
    public int getPlayersCount()
    {
        return this.playersCount;
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
    public IServersGroup getServersGroup()
    {
        return this.serversGroup;
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

    public void setPlayersCount(final Integer playersCount)
    {
        this.playersCount = playersCount;
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

    public void setConnectIp(final String connectIp)
    {
        this.connectIp = connectIp;
    }

    public void setConnectPort(final Integer connectPort)
    {
        this.connectPort = connectPort;
    }
}
