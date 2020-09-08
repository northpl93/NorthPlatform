package pl.north93.northplatform.features.bukkit.chat.admin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.chat.global.ChatManager;
import pl.north93.northplatform.api.chat.global.ChatPlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class AdminChatListener implements AutoListener
{
    @Inject
    private ChatManager      chatManager;
    @Inject
    private AdminChatService adminChatService;

    @EventHandler
    public void joinPlayerToAdminChat(final PlayerJoinEvent event)
    {
        final INorthPlayer player = INorthPlayer.wrap(event.getPlayer());

        if (! player.hasPermission("basefeatures.cmd.adminchat"))
        {
            return;
        }

        final ChatPlayer chatPlayer = this.chatManager.getPlayer(player.getIdentity());
        chatPlayer.joinRoom(this.adminChatService.getAdminRoom());
    }
}
