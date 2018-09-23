package pl.north93.zgame.daemon;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.daemon.DaemonRpc;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.daemon.network.DaemonInfoHandler;
import pl.north93.zgame.daemon.servers.LocalServerInstance;
import pl.north93.zgame.daemon.servers.LocalServersManager;

@Slf4j
public class DaemonRpcImpl implements DaemonRpc
{
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
            log.error("Received server stop request, but server {} cant be found.", serverUuid);
            return;
        }

        if (instance.isStopped())
        {
            log.error("Received server stop request, but server {} is already stopped.", serverUuid);
            return;
        }

        instance.getConsole().executeCommand("minecraft:stop");
    }
}
