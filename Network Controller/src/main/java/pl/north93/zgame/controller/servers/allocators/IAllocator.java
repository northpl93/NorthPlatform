package pl.north93.zgame.controller.servers.allocators;

import pl.north93.zgame.api.global.deployment.serversgroup.ManagedServersGroup;
import pl.north93.zgame.controller.servers.IServerCountManager;

public interface IAllocator
{
    void doAllocation(IServerCountManager serverCountManager, ManagedServersGroup serversGroup);
}
