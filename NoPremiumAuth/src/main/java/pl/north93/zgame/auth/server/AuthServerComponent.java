package pl.north93.zgame.auth.server;

import org.bukkit.Bukkit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.auth.sharedimpl.AuthManagerImpl;

public class AuthServerComponent extends Component
{
    @Inject
    private BukkitApiCore   apiCore;
    private AuthManagerImpl authManager;

    @Override
    protected void enableComponent()
    {
        this.authManager = new AuthManagerImpl();
        Bukkit.getPluginManager().registerEvents(new PlayerListeners(this.authManager), this.apiCore.getPluginMain());
    }

    @Override
    protected void disableComponent()
    {
    }

    public AuthManagerImpl getAuthManager()
    {
        return this.authManager;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("authManager", this.authManager).toString();
    }
}
