package pl.north93.zgame.api.bukkit.server.impl;

import org.bukkit.Bukkit;

import pl.north93.zgame.api.bukkit.server.IBukkitServerManager;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.server.IServerRpc;

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
}
