package pl.north93.zgame.webauth.server;

import static pl.north93.zgame.api.global.redis.rpc.Targets.networkController;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.webauth.IWebAuthManager;

public class WebAuthServerComponent extends Component
{
    @Inject
    private IRpcManager     rpcManager;
    private IWebAuthManager webAuthManager;

    @Override
    protected void enableComponent()
    {
        this.webAuthManager = this.rpcManager.createRpcProxy(IWebAuthManager.class, networkController());
    }

    @Override
    protected void disableComponent()
    {
    }

    public IWebAuthManager getWebAuthManager()
    {
        return this.webAuthManager;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
