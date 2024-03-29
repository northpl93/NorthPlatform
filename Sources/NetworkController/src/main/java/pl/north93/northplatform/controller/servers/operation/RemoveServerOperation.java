package pl.north93.northplatform.controller.servers.operation;

import static java.util.Comparator.comparing;


import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;

import lombok.ToString;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.impl.servers.ServerDto;
import pl.north93.northplatform.api.global.network.server.IServerRpc;
import pl.north93.northplatform.api.global.network.server.IServersManager;
import pl.north93.northplatform.api.global.network.server.Server;
import pl.north93.northplatform.api.global.redis.observable.Value;
import pl.north93.northplatform.api.global.redis.rpc.exceptions.RpcException;
import pl.north93.northplatform.controller.servers.groups.LocalManagedServersGroup;

@ToString(of = {"serversGroup", "ourServer"})
public class RemoveServerOperation extends AutoScalerOperation
{
    @Inject
    private IServersManager serversManager;
    private final LocalManagedServersGroup serversGroup;
    private Value<ServerDto> ourServer;

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

        this.ourServer = this.serversManager.unsafe().getServerDto(serverToShutdown.getUuid());

        final IServerRpc serverRpc = this.serversManager.getServerRpc(serverToShutdown);
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

        final IServerRpc serverRpc = this.serversManager.getServerRpc(serverDto);
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
        final Predicate<Server> isShutdownScheduled = Server::isShutdownScheduled;
        final Comparator<Server> serverComparator = comparing(Server::getPlayersCount);

        final Set<Server> servers = this.serversManager.inGroup(this.serversGroup.getName());
        return servers.stream()
                      .filter(isShutdownScheduled.negate()) // nie chcemy serwerów ktore juz sie wylaczaja
                      .min(serverComparator) // bierzemy ten z najmniejsza iloscia graczy
                      .orElse(null);
    }
}
