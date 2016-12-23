package pl.north93.zgame.skyblock.server;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.Targets;
import pl.north93.zgame.skyblock.api.ISkyBlockManager;

public class SkyBlockServer extends Component
{
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager      rpcManager;
    private ISkyBlockManager skyBlockManager;

    @Override
    protected void enableComponent()
    {
        this.skyBlockManager = this.rpcManager.createRpcProxy(ISkyBlockManager.class, Targets.networkController());
    }

    @Override
    protected void disableComponent()
    {
    }

    public ISkyBlockManager getSkyBlockManager()
    {
        return this.skyBlockManager;
    }
}
