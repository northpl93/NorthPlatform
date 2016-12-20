package pl.north93.zgame.controller;

import pl.north93.zgame.api.global.Platform;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.NetworkControllerRpc;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;

public class NetworkControllerCore extends Component
{
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager rpcManager;

    @Override
    protected void enableComponent()
    {
        this.getApiCore().getLogger().info("Starting NetworkController...");
        if (this.getApiCore().getPlatform() == Platform.BUNGEE) // on standalone platform context will be added automatically from getId()
        {
            this.rpcManager.addListeningContext("controller");
        }
        this.rpcManager.addRpcImplementation(NetworkControllerRpc.class, new NetworkControllerRpcImpl());
    }

    @Override
    protected void disableComponent()
    {
        this.getApiCore().getLogger().info("Network Controller stopped!");
    }
}
