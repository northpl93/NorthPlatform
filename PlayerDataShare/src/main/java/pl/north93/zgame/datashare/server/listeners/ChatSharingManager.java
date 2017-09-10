package pl.north93.zgame.datashare.server.listeners;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

import static pl.north93.zgame.api.global.utils.lang.StringUtils.asString;


import java.text.MessageFormat;
import java.util.Formatter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.cooldown.CooldownEntry;
import org.diorite.utils.cooldown.CooldownManager;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriber;
import pl.north93.zgame.datashare.api.DataSharingGroup;
import pl.north93.zgame.datashare.api.chat.ChatMessage;
import pl.north93.zgame.datashare.sharedimpl.PlayerDataShareComponent;

public class ChatSharingManager implements Listener
{
    private static final long COOLDOWN_TIME = TimeUnit.SECONDS.toMillis(15);
    @Inject
    private BukkitApiCore            apiCore;
    @Inject
    private RedisSubscriber          redisSubscriber;
    @Inject
    private TemplateManager          msgPack;
    @Inject
    private PlayerDataShareComponent shareComponent;
    @Inject @Messages("PlayerDataShare")
    private MessagesBox              messages;
    private CooldownManager<UUID>    chatCooldown;
    private UUID                     serverId;
    private boolean                  isChatEnabled;
    private DataSharingGroup         group;

    public void start(final DataSharingGroup group)
    {
        this.group = group;
        this.redisSubscriber.subscribe("broadcast:" + group.getName(), this::onBroadcast);
        this.redisSubscriber.subscribe("ann:" + group.getName(), this::onAnn);
        if (! group.getShareChat())
        {
            return;
        }
        this.serverId = this.apiCore.getServerId();
        this.chatCooldown = CooldownManager.createManager(100);
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
        final String message = translateAlternateColorCodes('&', asString(bytes));
        for (final Player player : Bukkit.getOnlinePlayers())
        {
            player.sendMessage(message);
        }
    }

    private void onAnn(final String channel, final byte[] bytes)
    {
        final String message = asString(bytes);
        for (final Player player : Bukkit.getOnlinePlayers())
        {
            player.sendTitle(message, null);
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
        final JoiningPolicy chatPolicy = this.shareComponent.getDataShareManager().getChatPolicy(this.group);
        final Player player = event.getPlayer();

        if (! player.hasPermission("chat.bypass"))
        {
            final boolean chatEnabled;
            switch(chatPolicy)
            {
                case ONLY_ADMIN: chatEnabled = false; break;
                case ONLY_VIP: chatEnabled = player.hasPermission("chat.vip"); break;
                default: chatEnabled = true;
            }

            if (! chatEnabled)
            {
                player.sendMessage(translateAlternateColorCodes('&', this.messages.getMessage(player.spigot().getLocale(), "chat.is_now_disabled")));
                event.setCancelled(true);
            }
            else if (! this.chatCooldown.hasExpiredOrAdd(player.getUniqueId(), COOLDOWN_TIME))
            {
                final CooldownEntry<UUID> entry = this.chatCooldown.getEntry(player.getUniqueId());
                final long time = (entry.getStartTime() + entry.getCooldownTime() - System.currentTimeMillis()) / 1000;
                final String message = MessageFormat.format(this.messages.getMessage(player.spigot().getLocale(), "chat.cooldown"), time);
                player.sendMessage(translateAlternateColorCodes('&', message));
                event.setCancelled(true);
            }
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
