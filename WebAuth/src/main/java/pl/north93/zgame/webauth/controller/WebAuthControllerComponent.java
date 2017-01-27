package pl.north93.zgame.webauth.controller;

import static pl.north93.zgame.api.global.cfg.ConfigUtils.loadConfigFile;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.webauth.IWebAuthManager;

public class WebAuthControllerComponent extends Component
{
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager rpcManager;

    @Override
    protected void enableComponent()
    {
        final WebAuthConfig config = loadConfigFile(WebAuthConfig.class, this.getApiCore().getFile("webauth.yml"));
        this.rpcManager.addRpcImplementation(IWebAuthManager.class, new WebAuthManagerImpl(config));
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
