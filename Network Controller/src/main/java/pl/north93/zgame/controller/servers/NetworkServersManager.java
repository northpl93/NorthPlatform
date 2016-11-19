package pl.north93.zgame.controller.servers;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.deployment.RemoteDaemon;
import pl.north93.zgame.api.global.deployment.ServersGroup;
import pl.north93.zgame.api.global.network.server.ServerImpl;
import pl.north93.zgame.controller.NetworkControllerCore;
import pl.north93.zgame.controller.servers.allocators.AllocationProcessor;

public class NetworkServersManager extends Thread implements INetworkServersManager, IServerCountManager
{
    private final NetworkControllerCore networkController;
    private final AllocationProcessor   allocationProcessor;

    public NetworkServersManager(final NetworkControllerCore networkControllerCore)
    {
        super("Servers Manager");
        this.networkController = networkControllerCore;
        this.allocationProcessor = new AllocationProcessor(this);
    }

    @Override
    public void run()
    {
        while (true) // TODO safe stop
        {
            final List<ServersGroup> serversGroups = this.networkController.getConfigBroadcaster().getServersGroups().getGroups();
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

    }

    @Override
    public IServerCountManager getServerCountManager()
    {
        return this;
    }

    @Override
    public long getServersCount(final ServersGroup serversGroup)
    {
        return API.getNetworkManager().getServers().stream()
                  .filter(server -> {
                      final Optional<ServersGroup> sGroup = server.getServersGroup();
                      return sGroup.isPresent() && sGroup.get().equals(serversGroup);
                  }).count();
    }

    @Override
    public void addServers(final ServersGroup serversGroup, final long servers)
    {
        API.getLogger().info("Adding " + servers + " servers to group " + serversGroup.getName());
        for (int i = 0; i < servers; i++)
        {
            final ServerImpl server = ServerFactory.INSTANCE.createNewServer(serversGroup);
            server.sendUpdate(); // send server data to redis.
            this.allocationProcessor.queueServerDeployment(server); // queue server for deployment.
        }
    }

    @Override
    public void removeServers(final ServersGroup serversGroup, final long servers)
    {
        API.getLogger().info("Removing " + servers + " servers from group " + serversGroup.getName());
        // TODO
    }
}
