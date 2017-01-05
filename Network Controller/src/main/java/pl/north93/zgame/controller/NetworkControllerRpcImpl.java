package pl.north93.zgame.controller;

import java.util.UUID;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.network.NetworkControllerRpc;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerImpl;
import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.api.global.redis.observable.Value;

public class NetworkControllerRpcImpl implements NetworkControllerRpc
{
    @Override
    public void ping()
    {
    }

    @Override
    public void stopController()
    {
        API.getLogger().info("Received stop request from network.");
        API.getPlatformConnector().stop();
    }

    @Override
    public void updateServerState(final UUID serverId, final ServerState serverState)
    {
        final Value<Server> server = API.getNetworkManager().getServer(serverId);
        if (!server.isAvailable())
        {
            API.getLogger().warning("Not found server with ID " + serverId + " while updating server state to " + serverState);
            return;
        }
        ((ServerImpl) server.get()).setServerState(serverState);
        server.upload();
    }

    @Override
    public void removeServer(final UUID serverId)
    {
        final Value<Server> server = API.getNetworkManager().getServer(serverId);
        if (!server.isAvailable())
        {
            API.getLogger().warning("Not found server with ID " + serverId + " while removing server ");
            return;
        }
        server.delete();
        API.getLogger().info("Removed server with UUID: " + serverId);
    }
}
