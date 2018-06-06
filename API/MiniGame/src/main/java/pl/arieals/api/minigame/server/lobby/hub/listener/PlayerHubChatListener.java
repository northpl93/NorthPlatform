package pl.arieals.api.minigame.server.lobby.hub.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.arieals.api.minigame.server.lobby.hub.event.PlayerSwitchedHubEvent;
import pl.arieals.api.minigame.server.lobby.hub.HubWorld;
import pl.north93.zgame.api.bukkit.server.IBukkitExecutor;
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
    private ChatManager     chatManager;
    @Inject
    private IBukkitExecutor bukkitExecutor;
    @Inject
    private MiniGameServer  miniGameServer;

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
        if (player.isInRoom(newChatRoom))
        {
            // jesli gracz przechodzi z innej instancji huba to wystepuje race condition
            // z racji tego jak dziala Bungee. Opozniamy ponowne dodanie gracza do pokoju.
            this.delayRoomJoin(player, newChatRoom);
        }
        else
        {
            player.joinRoom(newChatRoom);
        }
    }

    @EventHandler
    public void leaveRoomWhenPlayerQuitHubServer(final PlayerQuitEvent event)
    {
        final Player player = event.getPlayer();
        final ChatPlayer chatPlayer = this.chatManager.getPlayer(Identity.of(player));

        if (! chatPlayer.isOnline())
        {
            // gdy gracz wychodzi z sieci to tu będzie false
            // unikamy wyjątku PlayerOfflineException
            return;
        }

        final LobbyManager lobbyManager = this.miniGameServer.getServerManager();
        final HubWorld hubWorld = lobbyManager.getLocalHub().getHubWorld(player);

        if (hubWorld == null)
        {
            // teoretycznie nigdy tak nie powinno się stać, ale warto dmuchać na zimne
            return;
        }

        chatPlayer.leaveRoom(hubWorld.getChatRoom());
    }

    private void delayRoomJoin(final ChatPlayer player, final ChatRoom room)
    {
        this.bukkitExecutor.syncLater(10, () ->
        {
            if (player.isOnline())
            {
                player.joinRoom(room);
            }
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
