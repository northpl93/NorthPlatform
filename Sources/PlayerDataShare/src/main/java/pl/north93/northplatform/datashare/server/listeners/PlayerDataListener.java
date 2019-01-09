package pl.north93.northplatform.datashare.server.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.server.IBukkitExecutor;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.redis.subscriber.SubscriptionHandler;
import pl.north93.northplatform.api.global.serializer.platform.NorthSerializer;
import pl.north93.northplatform.datashare.server.PlayerDataShareServer;
import pl.north93.northplatform.datashare.sharedimpl.DataContainer;
import pl.north93.northplatform.datashare.sharedimpl.PlayerDataShareComponent;

@Slf4j
public class PlayerDataListener implements SubscriptionHandler
{
    @Inject
    private IBukkitExecutor          bukkitExecutor;
    @Inject
    private PlayerDataShareServer    shareServer;
    @Inject
    private PlayerDataShareComponent playerDataShare;
    @Inject
    private NorthSerializer<byte[]>  msgPack;

    @Override
    public void handle(final String channel, final byte[] message)
    {
        final DataContainer dataContainer = this.msgPack.deserialize(DataContainer.class, message);

        final Player player = Bukkit.getPlayer(dataContainer.getPlayerId());
        if (player == null)
        {
            return;
        }

        this.bukkitExecutor.sync(() ->
        {
            //if (player.isDataLoaded()) // todo
            if (true)
            {
                return; // skip data loading...
            }

            this.playerDataShare.getDataShareManager().applyDataTo(this.shareServer.getMyGroup(), player, dataContainer);

            log.info("Received data of player {} by redis publish", player.getName());
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
