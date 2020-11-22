package pl.north93.northplatform.webauth.controller;

import static pl.north93.northplatform.api.global.utils.ConfigUtils.loadConfig;


import java.io.File;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.redis.rpc.IRpcManager;
import pl.north93.northplatform.webauth.IWebAuthManager;

public class WebAuthControllerComponent extends Component
{
    @Inject
    private IRpcManager rpcManager;

    @Override
    protected void enableComponent()
    {
        final File configFile = this.getApiCore().getFile("webauth.xml");
        final WebAuthConfig config = loadConfig(WebAuthConfig.class, configFile);

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
