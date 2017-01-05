package pl.north93.zgame.controller.servers;

import pl.north93.zgame.api.global.deployment.serversgroup.ManagedServersGroup;

public interface IServerCountManager
{
    long getServersCount(ManagedServersGroup serversGroup);

    void addServers(ManagedServersGroup serversGroup, long servers);

    void removeServers(ManagedServersGroup serversGroup, long servers);
}
