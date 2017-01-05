package pl.north93.zgame.controller.servers.allocators;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.deployment.RemoteDaemon;
import pl.north93.zgame.api.global.deployment.ServersAllocatorType;
import pl.north93.zgame.api.global.deployment.serversgroup.ManagedServersGroup;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.controller.servers.INetworkServersManager;

/**
 * Klasa przekazująca proces alokacji do odpowiedniego alokatora
 */
public class AllocationProcessor
{
    private final INetworkServersManager serversManager;
    private final ServerDistributor      serverDistributor;
    private final Queue<Server>          deploymentQueue;

    public AllocationProcessor(final INetworkServersManager serversManager)
    {
        this.serversManager = serversManager;
        this.serverDistributor = new ServerDistributor();
        this.deploymentQueue = new ConcurrentLinkedQueue<>();
    }

    public void processTasks(final Set<RemoteDaemon> daemons, final List<ManagedServersGroup> serversGroup)
    {
        serversGroup.forEach(this::processAllocation);
        this.handleDeploymentQueue(daemons);
    }

    public void queueServerDeployment(final Server server)
    {
        if (! server.isLaunchedViaDaemon() || server.getServerState() != ServerState.ALLOCATING)
        {
            throw new IllegalArgumentException();
        }
        this.deploymentQueue.add(server);
    }

    private void processAllocation(final ManagedServersGroup serversGroup)
    {
        final ServersAllocatorType allocatorType = serversGroup.getAllocatorConfiguration().getAllocatorType();
        final IAllocator allocator = AllocatorFactory.INSTANCE.getAllocator(allocatorType);

        allocator.doAllocation(this.serversManager.getServerCountManager(), serversGroup);
    }

    private void handleDeploymentQueue(final Set<RemoteDaemon> daemons)
    {
        while (! this.deploymentQueue.isEmpty())
        {
            final Server nextServer = this.deploymentQueue.element();
            final RemoteDaemon daemon = this.serverDistributor.findBestDaemonFor(daemons, nextServer);
            if (daemon == null)
            {
                // nie znaleziono wolnego demona
                // więc nie ma sensu szukać iść dalej ani szukać
                // demona dla pozostałych serwerów
                break;
            }

            this.deploymentQueue.poll(); // remove server from queue
            // push server deployment to found daemon.
            // daemon will update server state
            daemon.getRpc().deployServer(nextServer.getUuid(), nextServer.getServerPattern().getPatternName());
            API.getLogger().info("Deploying server " + nextServer.getUuid() + " on daemon " + daemon.getName());
        }
    }
}
