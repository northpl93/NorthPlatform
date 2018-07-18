package pl.north93.zgame.datashare.server.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.north93.zgame.api.bukkit.server.IBukkitExecutor;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.datashare.api.DataSharingGroup;
import pl.north93.zgame.datashare.api.IDataShareManager;
import pl.north93.zgame.datashare.server.PlayerDataShareServer;
import pl.north93.zgame.datashare.sharedimpl.PlayerDataShareComponent;

public class PlayerJoinListener implements Listener
{
    private final Logger logger = LoggerFactory.getLogger(PlayerJoinListener.class);
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

            this.logger.info("Data of player {} is loaded by onJoin event. Server group: {}", player.getName(), myGroup.getName());
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
