package pl.north93.zgame.skyblock.manager;

import pl.north93.zgame.api.global.cfg.ConfigUtils;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.skyblock.api.ISkyBlockManager;
import pl.north93.zgame.skyblock.api.cfg.SkyBlockConfig;

public class SkyBlockManager extends Component
{
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager    rpcManager;
    private SkyBlockConfig skyBlockConfig;

    @Override
    protected void enableComponent()
    {
        if (!this.getApiCore().getId().equals("controller"))
        {
            return; // for developer env
        }
        this.skyBlockConfig = ConfigUtils.loadConfigFile(SkyBlockConfig.class, this.getApiCore().getFile("skyblock.yml"));
        this.rpcManager.addRpcImplementation(ISkyBlockManager.class, new SkyBlockManagerImpl(this));
    }

    @Override
    protected void disableComponent()
    {
    }

    public SkyBlockConfig getSkyBlockConfig()
    {
        return this.skyBlockConfig;
    }
}
