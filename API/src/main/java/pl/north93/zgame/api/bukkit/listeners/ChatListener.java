package pl.north93.zgame.api.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.network.NetworkPlayer;

public class ChatListener implements Listener
{
    @EventHandler
    public void onChat(final AsyncPlayerChatEvent event)
    {
        final NetworkPlayer networkPlayer = API.getNetworkManager().getNetworkPlayer(event.getPlayer().getName());
        event.setFormat(networkPlayer.getGroup().getChatFormat());
    }
}
