package pl.north93.zgame.controller.servers.operation;

import java.util.Comparator;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.daemon.DaemonDto;
import pl.north93.zgame.api.global.network.daemon.DaemonRpc;
import pl.north93.zgame.api.global.network.impl.ServerDto;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.api.global.network.server.group.ServersGroupDto;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.controller.servers.groups.LocalManagedServersGroup;

public class DeployServerOperation extends AutoScalerOperation
{
    @Inject
    private INetworkManager networkManager;
    private final LocalManagedServersGroup serversGroup;
    private Value<ServerDto> ourServer;

    public DeployServerOperation(final LocalManagedServersGroup serversGroup)
    {
        this.serversGroup = serversGroup;
    }

    @Override
    protected void startOperation()
    {
        final UUID serverId = UUID.randomUUID();
        final ServersGroupDto group = this.serversGroup.getAsDto();

        final DaemonDto bestDaemon = this.getBestDaemon();
        if (bestDaemon == null)
        {
            throw new IllegalStateException("Not found any daemon for deployment.");
        }

        final ServerDto serverDto = new ServerDto(serverId, true, group.getServersType(), ServerState.CREATING, group.getJoiningPolicy(), "", 0, group);
        // uploadujemy dane serwera do redisa i przypisujemy wartosc serwera
        this.ourServer = this.uploadServer(serverDto);

        final DaemonRpc daemonRpc = this.networkManager.getDaemons().getRpc(bestDaemon);
        // wysylamy do demona polecenie
        daemonRpc.deployServer(serverId, this.serversGroup.getConfig().getPattern());

        // wysylamy do wszystkich bungeecordow info o nowym serwerze
        this.networkManager.getProxies().addServer(serverDto);
    }

    @Override
    protected boolean cancel()
    {
        // na razie nie implementujemy mozliwosci anulowania operacji deployu
        return false;
    }

    @Override
    protected boolean isOpposite(final AutoScalerOperation operation)
    {
        return ! (operation instanceof DeployServerOperation);
    }

    @Override
    protected ScalerOperationState checkState()
    {
        final Server server = this.ourServer.get();
        if (server == null)
        {
            return ScalerOperationState.FAILED;
        }

        if (this.isServerFullyStarted(server))
        {
            return ScalerOperationState.COMPLETED;
        }

        return ScalerOperationState.IN_PROGRESS;
    }

    private DaemonDto getBestDaemon()
    {
        final Set<DaemonDto> daemons = this.networkManager.getDaemons().all();
        return daemons.stream()
                      .filter(DaemonDto::isAcceptingServers)
                      .sorted(new DaemonComparator())
                      .findFirst().orElse(null);
    }

    private Value<ServerDto> uploadServer(final ServerDto serverDto)
    {
        final Value<ServerDto> value = this.networkManager.getServers().unsafe().getServerDto(serverDto.getUuid());
        value.set(serverDto);
        return value;
    }

    private boolean isServerFullyStarted(final Server server)
    {
        final ServerState state = server.getServerState();
        return state == ServerState.WORKING || state == ServerState.STOPPING || state == ServerState.STOPPED;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serversGroup", this.serversGroup).append("ourServer", this.ourServer).toString();
    }
}

class DaemonComparator implements Comparator<DaemonDto>
{
    @Override
    public int compare(final DaemonDto o1, final DaemonDto o2)
    {
        final double d1RamPct = (double) o1.getRamUsed() / o1.getMaxRam() * 100D;
        final double d2RamPct = (double) o2.getRamUsed() / o2.getMaxRam() * 100D;

        return (int) (d1RamPct - d2RamPct);
    }
}