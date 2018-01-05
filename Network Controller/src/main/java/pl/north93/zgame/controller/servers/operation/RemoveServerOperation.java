package pl.north93.zgame.controller.servers.operation;

import static org.diorite.utils.function.FunctionUtils.not;


import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.math.DioriteRandomUtils;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.impl.ServerDto;
import pl.north93.zgame.api.global.network.server.IServerRpc;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.redis.rpc.exceptions.RpcException;
import pl.north93.zgame.controller.servers.groups.LocalManagedServersGroup;

public class RemoveServerOperation extends AutoScalerOperation
{
    @Inject
    private       INetworkManager          networkManager;
    private final LocalManagedServersGroup serversGroup;
    private       Value<ServerDto>         ourServer;

    public RemoveServerOperation(final LocalManagedServersGroup serversGroup)
    {
        this.serversGroup = serversGroup;
    }

    @Override
    protected boolean startOperation()
    {
        final Server serverToShutdown = this.getServerToShutdown();
        if (serverToShutdown == null)
        {
            // sygnalizujemy ze nie udalo sie nam rozpoczac operacji
            // bo nie znaleziono zadnego serwera do wylaczenia
            return false;
        }

        this.ourServer = this.networkManager.getServers().unsafe().getServerDto(serverToShutdown.getUuid());

        final IServerRpc serverRpc = this.networkManager.getServers().getServerRpc(serverToShutdown);
        serverRpc.setShutdownScheduled();

        return true;
    }

    @Override
    protected ScalerOperationState checkState()
    {
        if (this.ourServer == null) // teoretycznie nigdy nie wystapi bo anulujemy w startOperation
        {
            return ScalerOperationState.FAILED;
        }

        if (this.ourServer.isAvailable()) // ciagle jest klucz w redisie; serwer dziala lub sie wylacza
        {
            return ScalerOperationState.IN_PROGRESS;
        }
        return ScalerOperationState.COMPLETED;
    }

    @Override
    protected boolean cancel()
    {
        final ServerDto serverDto = this.ourServer.get();
        if (serverDto == null)
        {
            return false;
        }

        final IServerRpc serverRpc = this.networkManager.getServers().getServerRpc(serverDto);
        try
        {
            return serverRpc.cancelShutdown();
        }
        catch (final RpcException rpcException)
        {
            // gdy serwer juz nie odpowiada to pewnie sie wylaczyl
            return false;
        }
    }

    @Override
    protected boolean isOpposite(final AutoScalerOperation operation)
    {
        return ! (operation instanceof RemoveServerOperation);
    }

    private Server getServerToShutdown()
    {
        final Set<Server> servers = this.networkManager.getServers()
                                                       .inGroup(this.serversGroup.getName())
                                                       .stream().filter(not(Server::isShutdownScheduled))
                                                       .collect(Collectors.toSet());

        final Server randomServer = DioriteRandomUtils.getRandom(servers);
        if (randomServer == null)
        {
            return null;
        }

        return randomServer;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serversGroup", this.serversGroup).append("ourServer", this.ourServer).toString();
    }
}
