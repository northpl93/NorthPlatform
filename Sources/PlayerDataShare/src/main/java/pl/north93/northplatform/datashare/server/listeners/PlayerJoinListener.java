package pl.north93.northplatform.datashare.server.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.server.IBukkitExecutor;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.datashare.api.DataSharingGroup;
import pl.north93.northplatform.datashare.api.IDataShareManager;
import pl.north93.northplatform.datashare.server.PlayerDataShareServer;
import pl.north93.northplatform.datashare.sharedimpl.PlayerDataShareComponent;

@Slf4j
public class PlayerJoinListener implements Listener
{
    @Inject
    private IBukkitExecutor          executor;
    @Inject
    private PlayerDataShareComponent dataShareManager;
    @Inject
    private PlayerDataShareServer    dataShareServer;

    @EventHandler
    public void onJoin(final PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        final DataSharingGroup myGroup = this.dataShareServer.getMyGroup();
        final IDataShareManager manager = this.dataShareManager.getDataShareManager();

        this.executor.mixed(() -> manager.getFromRedisKey(myGroup, player.getUniqueId()), dataContainer ->
        {
            //if (player.isDataLoaded()) // todo
            if (true)
            {
                return; // skip loading if data is already loaded
            }
            manager.applyDataTo(myGroup, player, dataContainer);

            log.info("Data of player {} is loaded by onJoin event. Server group: {}", player.getName(), myGroup.getName());
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
