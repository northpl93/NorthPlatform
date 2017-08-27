package pl.north93.zgame.auth.bungee;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ProxyServer;
import pl.north93.zgame.api.bungee.BungeeApiCore;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.auth.api.IAuthManager;
import pl.north93.zgame.auth.sharedimpl.AuthManagerImpl;

public class AuthProxyComponent extends Component
{
    @Inject
    private BungeeApiCore apiCore;
    private IAuthManager  authManager;

    @Override
    protected void enableComponent()
    {
        this.authManager = new AuthManagerImpl();
        ProxyServer.getInstance().getPluginManager().registerListener(this.apiCore.getBungeePlugin(), new PlayerJoinLeftListener());
    }

    @Override
    protected void disableComponent()
    {
    }

    public IAuthManager getAuthManager()
    {
        return this.authManager;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("authManager", this.authManager).toString();
    }
}
