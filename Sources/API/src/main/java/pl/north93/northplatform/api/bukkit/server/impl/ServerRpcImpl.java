package pl.north93.northplatform.api.bukkit.server.impl;

import org.bukkit.Bukkit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.server.IServerRpc;

public class ServerRpcImpl implements IServerRpc
{
    @Inject
    private IBukkitServerManager serverManager;

    @Override
    public Integer getOnlinePlayers()
    {
        return Bukkit.getOnlinePlayers().size();
    }

    @Override
    public Boolean isShutdownScheduled()
    {
        return this.serverManager.isShutdownScheduled();
    }

    @Override
    public void setShutdownScheduled()
    {
        if (this.serverManager.isShutdownScheduled())
        {
            return;
        }
        this.serverManager.scheduleShutdown();
    }

    @Override
    public Boolean cancelShutdown()
    {
        try
        {
            this.serverManager.cancelShutdown();
            return true;
        }
        catch (final IllegalStateException e)
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
