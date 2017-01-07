package pl.north93.zgame.datashare.bungee;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ProxyServer;
import pl.north93.zgame.api.bungee.BungeeApiCore;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.Targets;
import pl.north93.zgame.datashare.api.IDataShareController;

public class PlayerDataShareBungee extends Component
{
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager          rpcManager;
    private IDataShareController controller;
    private BungeeApiCore        apiCore;

    @Override
    protected void enableComponent()
    {
        this.controller = this.rpcManager.createRpcProxy(IDataShareController.class, Targets.networkController());
        ProxyServer.getInstance().getPluginManager().registerListener(this.apiCore.getBungeePlugin(), new PlayerJoinListener());
    }

    @Override
    protected void disableComponent()
    {
    }

    public IDataShareController getController()
    {
        return this.controller;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
