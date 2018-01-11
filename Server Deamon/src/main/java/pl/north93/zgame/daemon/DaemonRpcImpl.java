package pl.north93.zgame.daemon;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.daemon.DaemonRpc;
import pl.north93.zgame.daemon.servers.LocalServerInstance;
import pl.north93.zgame.daemon.servers.LocalServersManager;

public class DaemonRpcImpl implements DaemonRpc
{
    @Inject
    private Logger logger;
    @Inject
    private DaemonComponent daemonCore;
    @Inject
    private LocalServersManager localServersManager;

    @Override
    public void setAcceptingNewServers(final Boolean isAcceptingNewServers)
    {
        this.daemonCore.getDaemonInfo().update(daemon ->
        {
            this.logger.log(Level.INFO, "Switched accepting new servers: " + isAcceptingNewServers);
            daemon.setAcceptingServers(isAcceptingNewServers);
        });
    }

    @Override
    public void deployServer(final UUID serverUuid, final String templateName)
    {
        this.localServersManager.deployServer(serverUuid, templateName);
    }

    @Override
    public void stopServer(final UUID serverUuid)
    {
        final LocalServerInstance instance = this.localServersManager.getInstance(serverUuid);
        if (instance == null)
        {
            this.logger.log(Level.SEVERE, "Received server stop request, but server {0} cant be found.", serverUuid);
            return;
        }

        if (instance.isStopped())
        {
            this.logger.log(Level.SEVERE, "Received server stop request, but server {0} is already stopped.", serverUuid);
            return;
        }

        instance.getConsole().executeCommand("minecraft:stop");
    }
}
