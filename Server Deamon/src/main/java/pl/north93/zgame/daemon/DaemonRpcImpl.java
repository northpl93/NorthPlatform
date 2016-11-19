package pl.north93.zgame.daemon;

import java.util.UUID;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.deployment.DaemonRpc;
import pl.north93.zgame.daemon.servers.ServerInstance;

public class DaemonRpcImpl implements DaemonRpc
{
    private final DaemonCore daemonCore;

    public DaemonRpcImpl(final DaemonCore daemonCore)
    {
        this.daemonCore = daemonCore;
    }

    @Override
    public void setAcceptingNewServers(final Boolean isAcceptingNewServers)
    {
        this.daemonCore.getDaemonInfo().setAcceptingServers(isAcceptingNewServers);
    }

    @Override
    public void deployServer(final UUID serverUuid, final String templateName)
    {
        API.getPlatformConnector().runTaskAsynchronously(() -> this.daemonCore.getServersManager().deployNewServer(serverUuid, templateName));
    }

    @Override
    public void stopServer(final UUID serverUuid)
    {
        final ServerInstance instance = this.daemonCore.getServersManager().getServer(serverUuid);
        if (instance == null)
        {
            API.getLogger().warning("Received server stop request, but server " + serverUuid + " cant be found.");
            return;
        }
        instance.executeCommand("stop");
    }
}
