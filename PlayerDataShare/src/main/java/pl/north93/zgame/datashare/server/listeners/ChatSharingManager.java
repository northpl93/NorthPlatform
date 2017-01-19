package pl.north93.zgame.datashare.server.listeners;

import static org.bukkit.ChatColor.translateAlternateColorCodes;


import java.util.Formatter;
import java.util.ResourceBundle;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriber;
import pl.north93.zgame.datashare.api.DataSharingGroup;
import pl.north93.zgame.datashare.api.chat.ChatMessage;
import pl.north93.zgame.datashare.sharedimpl.PlayerDataShareComponent;
import redis.clients.util.SafeEncoder;

public class ChatSharingManager implements Listener
{
    private BukkitApiCore    apiCore;
    @InjectComponent("API.Database.Redis.Subscriber")
    private RedisSubscriber  redisSubscriber;
    @InjectComponent("API.Database.Redis.MessagePackSerializer")
    private TemplateManager  msgPack;
    @InjectComponent("PlayerDataShare.SharedImpl")
    private PlayerDataShareComponent shareComponent;
    @InjectResource(bundleName = "PlayerDataShare")
    private ResourceBundle   messages;
    private UUID             serverId;
    private boolean          isChatEnabled;
    private DataSharingGroup group;

    public void start(final DataSharingGroup group)
    {
        this.group = group;
        this.redisSubscriber.subscribe("broadcast:" + group.getName(), this::onBroadcast);
        if (! group.getShareChat())
        {
            return;
        }
        this.serverId = this.apiCore.getServer().get().getUuid();
        Bukkit.getPluginManager().registerEvents(this, this.apiCore.getPluginMain());
        this.redisSubscriber.subscribe("chat:" + group.getName(), this::onRemoteChat);
    }

    public void stop()
    {
        if (! this.group.getShareChat())
        {
            return;
        }
        AsyncPlayerChatEvent.getHandlerList().unregister(this);
        this.redisSubscriber.unSubscribe("chat:" + this.group.getName());
    }

    private void onBroadcast(final String channel, final byte[] bytes)
    {
        final String message = SafeEncoder.encode(bytes);
        for (final Player player : Bukkit.getOnlinePlayers())
        {
            player.sendMessage(message);
        }
    }

    private void onRemoteChat(final String channel, final byte[] bytes)
    {
        final ChatMessage chatMessage = this.msgPack.deserialize(ChatMessage.class, bytes);
        if (chatMessage.getSourceServerId().equals(this.serverId))
        {
            return;
        }

        for (final Player player : Bukkit.getOnlinePlayers())
        {
            player.sendMessage(chatMessage.getMessage());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onChatNormal(final AsyncPlayerChatEvent event)
    {
        final boolean chatEnabled = this.shareComponent.getDataShareManager().isChatEnabled(this.group);
        final Player player = event.getPlayer();
        if (! chatEnabled && ! player.hasPermission("chat.bypass"))
        {
            player.sendMessage(translateAlternateColorCodes('&', this.messages.getString("chat.is_now_disabled")));
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChatMonitor(final AsyncPlayerChatEvent event)
    {
        final Formatter formatter = new Formatter();
        final String renderedMessage = formatter.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage()).toString();
        final ChatMessage chatMessage = new ChatMessage(this.serverId, event.getPlayer().getUniqueId(), renderedMessage);
        this.redisSubscriber.publish("chat:" + this.group.getName(), this.msgPack.serialize(ChatMessage.class, chatMessage));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serverId", this.serverId).append("group", this.group).toString();
    }
}
