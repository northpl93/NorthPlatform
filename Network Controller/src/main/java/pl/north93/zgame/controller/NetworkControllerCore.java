package pl.north93.zgame.controller;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.network.NetworkControllerRpc;
import pl.north93.zgame.controller.servers.NetworkServersManager;

public class NetworkControllerCore
{
    private final ConfigBroadcaster     networkMetaBroadcaster = new ConfigBroadcaster();
    private final NetworkServersManager serversManager         = new NetworkServersManager();

    public void start()
    {
        API.getLogger().info("Starting NetworkController...");
        API.getRpcManager().addListeningContext("controller");
        API.getRpcManager().addRpcImplementation(NetworkControllerRpc.class, new NetworkControllerRpcImpl());
        this.networkMetaBroadcaster.start();
    }

    public void stop()
    {
        this.networkMetaBroadcaster.stop();
    }
}
