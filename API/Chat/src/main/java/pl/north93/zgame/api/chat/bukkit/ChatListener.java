package pl.north93.zgame.api.chat.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.chat.global.ChatManager;
import pl.north93.zgame.api.chat.global.ChatPlayer;
import pl.north93.zgame.api.chat.global.ChatRoom;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.Identity;

public class ChatListener implements AutoListener
{
    @Inject
    private ChatManager chatManager;


    @EventHandler
    public void routeChat(final AsyncPlayerChatEvent event)
    {
        final Player player = event.getPlayer();

        final ChatPlayer chatPlayer = this.chatManager.getPlayer(Identity.of(player));
        final ChatRoom mainRoom = chatPlayer.getMainRoom();

        if (mainRoom == null)
        {
            return;
        }


    }
}
