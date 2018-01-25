package pl.north93.zgame.datashare.server.listeners;

import java.text.MessageFormat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.server.IBukkitExecutor;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.subscriber.SubscriptionHandler;
import pl.north93.zgame.datashare.server.PlayerDataShareServer;
import pl.north93.zgame.datashare.sharedimpl.DataContainer;
import pl.north93.zgame.datashare.sharedimpl.PlayerDataShareComponent;

public class PlayerDataListener implements SubscriptionHandler
{
    @Inject
    private BukkitApiCore            apiCore;
    @Inject
    private IBukkitExecutor          bukkitExecutor;
    @Inject
    private PlayerDataShareServer    shareServer;
    @Inject
    private PlayerDataShareComponent playerDataShare;
    @Inject
    private TemplateManager          msgPack;

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
            if (player.isDataLoaded())
            {
                return; // skip data loading...
            }

            this.playerDataShare.getDataShareManager().applyDataTo(this.shareServer.getMyGroup(), player, dataContainer);

            final String log = "Received data of player {0} by redis publish";
            this.apiCore.getLogger().info(MessageFormat.format(log, player.getName()));
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
