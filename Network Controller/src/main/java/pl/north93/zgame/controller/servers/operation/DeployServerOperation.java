package pl.north93.zgame.controller.servers.operation;

import java.util.UUID;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
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

        System.out.println("DeployServerOperation#startOperation()");
        // todo find daemon and get his ip/port

        final ServerDto serverDto = new ServerDto(serverId, true, group.getServersType(), ServerState.CREATING, group.getJoiningPolicy(), "", 0, group);

        // uploadujemy dane serwera do redisa i przypisujemy wartosc serwera
        this.ourServer = this.uploadServer(serverDto);
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
}
