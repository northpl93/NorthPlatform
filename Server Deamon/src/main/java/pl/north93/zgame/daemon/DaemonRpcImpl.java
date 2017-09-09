package pl.north93.zgame.daemon;

import java.util.UUID;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.daemon.DaemonRpc;
import pl.north93.zgame.daemon.servers.LocalServersManager;

public class DaemonRpcImpl implements DaemonRpc
{
    @Inject
    private DaemonComponent daemonCore;
    @Inject
    private LocalServersManager localServersManager;
    @Inject
    private ApiCore apiCore;

    @Override
    public void setAcceptingNewServers(final Boolean isAcceptingNewServers)
    {
        this.daemonCore.getDaemonInfo().update(daemon ->
        {
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
        /*final ServerInstance instance = this.daemonCore.getServersManager().getServer(serverUuid);
        if (instance == null)
        {
            this.apiCore.getLogger().log(Level.WARNING, "Received server stop request, but server {0} cant be found.", serverUuid);
            return;
        }
        instance.executeCommand("stop");*/
    }
}
