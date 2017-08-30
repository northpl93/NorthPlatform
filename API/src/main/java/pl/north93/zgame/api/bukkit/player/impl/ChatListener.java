package pl.north93.zgame.api.bukkit.player.impl;

import static org.bukkit.ChatColor.translateAlternateColorCodes;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.player.IBukkitPlayers;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class ChatListener implements Listener
{
    @Inject
    private IBukkitPlayers bukkitPlayers;

    @EventHandler(ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event)
    {
        // todo rewrite
        final INorthPlayer player = this.bukkitPlayers.getPlayer(event.getPlayer());
        final String newFormat = translateAlternateColorCodes('&', player.getGroup().getChatFormat());
        event.setFormat(newFormat);

        if (event.getPlayer().hasPermission("chat.colorize"))
        {
            event.setMessage(translateAlternateColorCodes('&', event.getMessage()));
        }

        if (! event.getPlayer().hasPermission("chat.rawMessage"))
        {
            event.setMessage(
                event.getMessage()
                 .replaceAll("(\\?){4,}", "?")
                 .replaceAll("(\\!){4,}", "!")
                 .replaceAll("usun potwierdz", "")
            );
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
