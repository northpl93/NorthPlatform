package pl.north93.zgame.daemon;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.daemon.DaemonRpc;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.daemon.network.DaemonInfoHandler;
import pl.north93.zgame.daemon.servers.LocalServerInstance;
import pl.north93.zgame.daemon.servers.LocalServersManager;

public class DaemonRpcImpl implements DaemonRpc
{
    private final Logger logger = LoggerFactory.getLogger(DaemonRpcImpl.class);
    @Inject
    private DaemonInfoHandler daemonInfoHandler;
    @Inject
    private LocalServersManager localServersManager;

    @Override
    public void setAcceptingNewServers(final Boolean isAcceptingNewServers)
    {
        this.daemonInfoHandler.setAcceptingNewServers(isAcceptingNewServers);
    }

    @Override
    public void deployServer(final UUID serverUuid, final String templateName)
    {
        final CompletableFuture<Server> future = this.localServersManager.scheduleServerDeployment(serverUuid, templateName);
    }

    @Override
    public void stopServer(final UUID serverUuid)
    {
        final LocalServerInstance instance = this.localServersManager.getInstance(serverUuid);
        if (instance == null)
        {
            this.logger.error("Received server stop request, but server {} cant be found.", serverUuid);
            return;
        }

        if (instance.isStopped())
        {
            this.logger.error("Received server stop request, but server {} is already stopped.", serverUuid);
            return;
        }

        instance.getConsole().executeCommand("minecraft:stop");
    }
}
