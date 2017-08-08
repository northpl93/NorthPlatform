package pl.north93.zgame.api.bukkit.player.impl;

import static org.bukkit.ChatColor.translateAlternateColorCodes;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;

public class ChatListener implements Listener
{
    @EventHandler(ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event)
    {
        // todo rewrite
        final Value<IOnlinePlayer> networkPlayer = API.getNetworkManager().getPlayers().unsafe().getOnline(event.getPlayer().getName());
        final String newFormat = translateAlternateColorCodes('&', networkPlayer.get().getGroup().getChatFormat());
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
}
