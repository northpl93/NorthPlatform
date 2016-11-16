package pl.north93.zgame.controller.servers.allocators;

import pl.north93.zgame.api.global.deployment.ServersGroup;
import pl.north93.zgame.controller.servers.IServerCountManager;

public interface IAllocator
{
    void doAllocation(IServerCountManager serverCountManager, ServersGroup serversGroup);
}
