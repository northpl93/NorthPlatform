package pl.north93.northplatform.datashare.bungee;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bungee.BungeeHostConnector;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.redis.rpc.IRpcManager;
import pl.north93.northplatform.api.global.redis.rpc.Targets;
import pl.north93.northplatform.datashare.api.IDataShareController;

public class PlayerDataShareBungee extends Component
{
    @Inject
    private IRpcManager rpcManager;
    @Inject
    private BungeeHostConnector hostConnector;
    private IDataShareController controller;

    @Override
    protected void enableComponent()
    {
        this.controller = this.rpcManager.createRpcProxy(IDataShareController.class, Targets.networkController());
        this.hostConnector.registerListeners(new PlayerJoinListener());
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
