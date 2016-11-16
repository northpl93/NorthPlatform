package pl.north93.zgame.controller.servers;

import pl.north93.zgame.api.global.deployment.ServersGroup;

public interface IServerCountManager
{
    long getServersCount(ServersGroup serversGroup);

    void addServers(ServersGroup serversGroup, long servers);

    void removeServers(ServersGroup serversGroup, long servers);
}
