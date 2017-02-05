package pl.north93.zgame.datashare.server;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.datashare.sharedimpl.PlayerDataShareComponent;

public class InventoryPersistTask implements Runnable
{
    private ApiCore                  apiCore;
    @InjectComponent("PlayerDataShare.SharedImpl")
    private PlayerDataShareComponent dataShareManager;
    @InjectComponent("PlayerDataShare.Bukkit")
    private PlayerDataShareServer    dataShareServer;

    @Override
    public void run()
    {
        this.apiCore.getLogger().info("[PlayerDataShare] Backing up players data...");
        for (final Player player : Bukkit.getOnlinePlayers())
        {
            this.dataShareManager.getDataShareManager().savePlayer(this.dataShareServer.getMyGroup(), player, false);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("dataShareManager", this.dataShareManager).append("dataShareServer", this.dataShareServer).toString();
    }
}
