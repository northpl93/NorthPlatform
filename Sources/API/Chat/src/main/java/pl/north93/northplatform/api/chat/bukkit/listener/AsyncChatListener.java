package pl.north93.northplatform.api.chat.bukkit.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.chat.bukkit.engine.ChatEngine;
import pl.north93.northplatform.api.chat.bukkit.engine.SendMessageResult;
import pl.north93.northplatform.api.chat.global.ChatManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class AsyncChatListener implements AutoListener
{
    @Inject
    private ChatManager chatManager;
    @Inject
    private ChatEngine  chatEngine;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void routeChat(final AsyncPlayerChatEvent event)
    {
        event.setCancelled(true);

        final Player player = event.getPlayer();

        final SendMessageResult result = this.chatEngine.sendMessageByPlayer(player, event.getMessage());



    }
}
