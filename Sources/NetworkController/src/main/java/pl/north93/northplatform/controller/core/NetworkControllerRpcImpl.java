package pl.north93.northplatform.controller.core;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.NetworkControllerRpc;
import pl.north93.northplatform.api.global.network.server.ServerState;

@Slf4j
public class NetworkControllerRpcImpl implements NetworkControllerRpc
{
    @Inject
    private ApiCore apiCore;

    @Override
    public void ping()
    {
    }

    @Override
    public void stopController()
    {
        log.info("Received stop request from network.");
        this.apiCore.getHostConnector().shutdownHost();
    }

    @Override
    public void updateServerState(final UUID serverId, final ServerState serverState)
    {
        /*final Value<Server> server = API.getNetworkManager().getServers().withUuid(serverId);
        if (!server.isAvailable())
        {
            API.getLogger().warning("Not found server with ID " + serverId + " while updating server state to " + serverState);
            return;
        }
        final ServerImpl serverImpl = (ServerImpl) server.get();
        serverImpl.setServerState(serverState);
        server.set(serverImpl);*/
        // TODO
        throw new UnsupportedOperationException("Todo");
    }

    @Override
    public void removeServer(final UUID serverId)
    {
        /*final Value<Server> server = API.getNetworkManager().getServer(serverId);
        if (!server.isAvailable())
        {
            API.getLogger().warning("Not found server with ID " + serverId + " while removing server ");
            return;
        }
        server.delete();
        API.getLogger().info("Removed server with UUID: " + serverId);*/
        // TODO
        throw new UnsupportedOperationException("Todo");
    }
}
