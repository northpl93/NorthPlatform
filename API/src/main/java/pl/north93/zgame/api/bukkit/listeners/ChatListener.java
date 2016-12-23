package pl.north93.zgame.api.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.network.NetworkPlayer;
import pl.north93.zgame.api.global.redis.observable.Value;

public class ChatListener implements Listener
{
    @EventHandler
    public void onChat(final AsyncPlayerChatEvent event)
    {
        final Value<NetworkPlayer> networkPlayer = API.getNetworkManager().getNetworkPlayer(event.getPlayer().getName());
        event.setFormat(networkPlayer.get().getGroup().getChatFormat());
    }
}
