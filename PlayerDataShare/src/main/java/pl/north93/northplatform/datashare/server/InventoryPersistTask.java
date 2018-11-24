package pl.north93.northplatform.datashare.server;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.datashare.sharedimpl.PlayerDataShareComponent;

@Slf4j
public class InventoryPersistTask implements Runnable
{
    @Inject
    private ApiCore                  apiCore;
    @Inject
    private PlayerDataShareComponent dataShareManager;
    @Inject
    private PlayerDataShareServer    dataShareServer;

    @Override
    public void run()
    {
        log.info("[PlayerDataShare] Backing up players data...");
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
