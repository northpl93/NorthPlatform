package pl.north93.zgame.datashare.server.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.subscriber.SubscriptionHandler;
import pl.north93.zgame.datashare.server.PlayerDataShareServer;
import pl.north93.zgame.datashare.sharedimpl.DataContainer;
import pl.north93.zgame.datashare.sharedimpl.PlayerDataShareComponent;

public class PlayerDataListener implements SubscriptionHandler
{
    private BukkitApiCore            apiCore;
    @InjectComponent("PlayerDataShare.Bukkit")
    private PlayerDataShareServer    shareServer;
    @InjectComponent("PlayerDataShare.SharedImpl")
    private PlayerDataShareComponent playerDataShare;
    @InjectComponent("API.Database.Redis.MessagePackSerializer")
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

        this.playerDataShare.getDataShareManager().applyDataTo(this.shareServer.getMyGroup(), player, dataContainer);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
