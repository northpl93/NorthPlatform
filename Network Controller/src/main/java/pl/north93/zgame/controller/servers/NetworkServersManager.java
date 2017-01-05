package pl.north93.zgame.controller.servers;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.deployment.RemoteDaemon;
import pl.north93.zgame.api.global.deployment.serversgroup.IServersGroup;
import pl.north93.zgame.api.global.deployment.serversgroup.ManagedServersGroup;
import pl.north93.zgame.api.global.network.server.ServerImpl;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.controller.ConfigBroadcaster;
import pl.north93.zgame.controller.servers.allocators.AllocationProcessor;

public class NetworkServersManager extends Component implements INetworkServersManager, IServerCountManager
{
    @InjectComponent("NetworkController.ConfigBroadcaster")
    private ConfigBroadcaster         configBroadcaster;
    @InjectComponent("API.Database.Redis.Observer")
    private IObservationManager       observationManager;
    private UnManagedServersLoader    unManagedServersLoader;
    private boolean                   working;
    private final AllocationProcessor allocationProcessor;


    public NetworkServersManager()
    {
        this.allocationProcessor = new AllocationProcessor(this);
    }

    @Override
    protected void enableComponent()
    {
        this.working = true;
        new Thread(this::thread, "Servers Manager").start();
        this.unManagedServersLoader = new UnManagedServersLoader();
        this.unManagedServersLoader.broadcastServers();
    }

    @Override
    protected void disableComponent()
    {
        this.working = false;
    }

    private void thread()
    {
        while (this.working)
        {
            final List<ManagedServersGroup> serversGroups = this.configBroadcaster.getServersGroups().getManagedGroups();
            final Set<RemoteDaemon> daemons = API.getNetworkManager().getDaemons();

            this.allocationProcessor.processTasks(daemons, serversGroups);
            try
            {
                synchronized (this)
                {
                    this.wait(1_000);
                }
            }
            catch (final InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        this.getApiCore().getLogger().info("Servers manager thread stopped...");
    }

    public UnManagedServersLoader getUnManagedServersLoader()
    {
        return this.unManagedServersLoader;
    }

    @Override
    public IServerCountManager getServerCountManager()
    {
        return this;
    }

    @Override
    public long getServersCount(final ManagedServersGroup serversGroup)
    {
        return API.getNetworkManager().getServers().stream()
                  .filter(server -> {
                      final Optional<IServersGroup> sGroup = server.getServersGroup();
                      return sGroup.isPresent() && sGroup.get().equals(serversGroup);
                  }).count();
    }

    @Override
    public void addServers(final ManagedServersGroup serversGroup, final long servers)
    {
        API.getLogger().info("Adding " + servers + " servers to group " + serversGroup.getName());
        for (int i = 0; i < servers; i++)
        {
            final ServerImpl server = ServerFactory.INSTANCE.createNewServer(serversGroup);
            this.observationManager.of(server).upload();
            this.allocationProcessor.queueServerDeployment(server); // queue server for deployment.
        }
    }

    @Override
    public void removeServers(final ManagedServersGroup serversGroup, final long servers)
    {
        API.getLogger().info("Removing " + servers + " servers from group " + serversGroup.getName());
        // TODO
    }
}
