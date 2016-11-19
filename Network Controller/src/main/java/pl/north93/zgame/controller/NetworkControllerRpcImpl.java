package pl.north93.zgame.controller;

import java.util.UUID;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.network.NetworkControllerRpc;
import pl.north93.zgame.api.global.network.server.ServerImpl;
import pl.north93.zgame.api.global.network.server.ServerState;

public class NetworkControllerRpcImpl implements NetworkControllerRpc
{
    @Override
    public void ping()
    {
    }

    @Override
    public void stopController()
    {
        API.getPlatformConnector().stop();
    }

    @Override
    public void updateServerState(final UUID serverId, final ServerState serverState)
    {
        final ServerImpl server = (ServerImpl) API.getNetworkManager().getServer(serverId);
        if (server == null)
        {
            API.getLogger().warning("Not found server with ID " + serverId + " while updating server state to " + serverState);
            return;
        }
        server.updateServerState(serverState);
    }

    @Override
    public void removeServer(final UUID serverId)
    {
        final ServerImpl server = (ServerImpl) API.getNetworkManager().getServer(serverId);
        if (server == null)
        {
            API.getLogger().warning("Not found server with ID " + serverId + " while removing server ");
            return;
        }
        server.delete();
        API.getLogger().info("Removed server with UUID: " + serverId);
    }
}
