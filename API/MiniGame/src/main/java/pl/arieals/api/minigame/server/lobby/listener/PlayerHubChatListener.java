package pl.arieals.api.minigame.server.lobby.listener;

import org.bukkit.event.EventHandler;

import pl.arieals.api.minigame.server.lobby.event.PlayerSwitchedHubEvent;
import pl.arieals.api.minigame.server.lobby.hub.HubWorld;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.chat.global.ChatManager;
import pl.north93.zgame.api.chat.global.ChatPlayer;
import pl.north93.zgame.api.chat.global.ChatRoom;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.Identity;

/**
 * Ogólna obsługa czatu na hubach.
 */
public class PlayerHubChatListener implements AutoListener
{
    @Inject
    private ChatManager chatManager;

    @EventHandler
    public void switchChatRoomOnHubSwitch(final PlayerSwitchedHubEvent event)
    {
        final ChatPlayer player = this.chatManager.getPlayer(Identity.of(event.getPlayer()));
        final HubWorld oldHub = event.getOldHub();
        final HubWorld newHub = event.getNewHub();

        if (oldHub != null)
        {
            final ChatRoom oldChatRoom = oldHub.getChatRoom();
            player.leaveRoom(oldChatRoom);
        }

        final ChatRoom newChatRoom = newHub.getChatRoom();
        player.joinRoom(newChatRoom);
    }
}
