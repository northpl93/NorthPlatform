package pl.north93.zgame.controller;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.network.NetworkControllerRpc;

public class NetworkControllerCore
{
    private final ConfigBroadcaster networkMetaBroadcaster = new ConfigBroadcaster();

    public void start()
    {
        API.getLogger().info("Starting NetworkController...");
        API.getRpcManager().addRpcImplementation(NetworkControllerRpc.class, new NetworkControllerRpcImpl());
        this.networkMetaBroadcaster.start();
    }

    public void stop()
    {
        this.networkMetaBroadcaster.stop();
    }
}
