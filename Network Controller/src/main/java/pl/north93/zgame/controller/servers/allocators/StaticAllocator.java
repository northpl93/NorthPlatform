package pl.north93.zgame.controller.servers.allocators;

import pl.north93.zgame.api.global.deployment.AllocationConfiguration;
import pl.north93.zgame.api.global.deployment.ServersGroup;
import pl.north93.zgame.controller.servers.IServerCountManager;

public class StaticAllocator implements IAllocator
{
    @Override
    public void doAllocation(final IServerCountManager serverCountManager, final ServersGroup serversGroup)
    {
        final AllocationConfiguration config = serversGroup.getAllocatorConfiguration();
        final long targetServerCount = Math.max(config.getMinServers(), config.getMaxServers());
        final long currentServerCount = serverCountManager.getServersCount(serversGroup);

        final long serverDelta = targetServerCount - currentServerCount;
        if (serverDelta < 0)
        {
            serverCountManager.removeServers(serversGroup, -serverDelta);
        }
        else if (serverDelta > 0)
        {
            serverCountManager.addServers(serversGroup, serverDelta);
        }
    }
}
