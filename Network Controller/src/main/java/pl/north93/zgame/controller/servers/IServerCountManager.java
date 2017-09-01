package pl.north93.zgame.controller.servers;

import pl.north93.zgame.controller.servers.cfg.ManagedServersGroupConfig;

public interface IServerCountManager
{
    long getServersCount(ManagedServersGroupConfig serversGroup);

    void addServers(ManagedServersGroupConfig serversGroup, long servers);

    void removeServers(ManagedServersGroupConfig serversGroup, long servers);
}
