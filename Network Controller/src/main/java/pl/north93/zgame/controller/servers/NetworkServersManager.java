package pl.north93.zgame.controller.servers;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.deployment.RemoteDaemon;
import pl.north93.zgame.api.global.deployment.ServersGroup;
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
    public void addServers(final ServersGroup serversGroup, final int servers)
    {

    }

    @Override
    public void removeServers(final ServersGroup serversGroup, final int servers)
    {

    }
}
