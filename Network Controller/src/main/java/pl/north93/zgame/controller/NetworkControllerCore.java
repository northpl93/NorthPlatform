package pl.north93.zgame.controller;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.Platform;
import pl.north93.zgame.api.global.network.NetworkControllerRpc;
import pl.north93.zgame.controller.servers.NetworkServersManager;

public class NetworkControllerCore
{
    private ConfigBroadcaster     networkMetaBroadcaster = new ConfigBroadcaster();
    private NetworkServersManager serversManager;

    public void start()
    {
        API.getLogger().info("Starting NetworkController...");
        if (API.getPlatform() == Platform.BUNGEE) // on standalone platform context will be added automatically from getId()
        {
            API.getRpcManager().addListeningContext("controller");
        }
        API.getRpcManager().addRpcImplementation(NetworkControllerRpc.class, new NetworkControllerRpcImpl());
        this.serversManager = new NetworkServersManager(this);
        this.networkMetaBroadcaster.start();
    }

    public void stop()
    {
        this.networkMetaBroadcaster.stop();
    }

    public ConfigBroadcaster getConfigBroadcaster()
    {
        return this.networkMetaBroadcaster;
    }

    public NetworkServersManager getServersManager()
    {
        return this.serversManager;
    }
}
