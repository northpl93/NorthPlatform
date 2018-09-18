package pl.arieals.api.minigame.server.lobby.hub.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.arieals.api.minigame.server.lobby.hub.HubWorld;
import pl.arieals.api.minigame.server.lobby.hub.event.PlayerSwitchedHubEvent;
import pl.arieals.api.minigame.shared.api.status.IPlayerStatus;
import pl.arieals.api.minigame.shared.api.status.IPlayerStatusManager;
import pl.arieals.api.minigame.shared.api.status.InHubStatus;
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
    private static final int RACE_CONDITION_WAIT = 20;
    @Inject
    private ChatManager          chatManager;
    @Inject
    private IPlayerStatusManager statusManager;
    @Inject
    private MiniGameServer       miniGameServer;

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

        player.joinRoom(newHub.getChatRoom());
    }

    @EventHandler
    public void leaveRoomWhenPlayerQuitHubServer(final PlayerQuitEvent event)
    {
        final Player player = event.getPlayer();
        final Identity identity = Identity.of(player);

        final IPlayerStatus status = this.statusManager.getPlayerStatus(identity);
        if (status.getType() == IPlayerStatus.StatusType.OFFLINE)
        {
            // gdy gracz wyszedl z sieci to nic nie musimy robic
            return;
        }

        final LobbyManager lobbyManager = this.miniGameServer.getServerManager();
        final HubWorld hubWorld = lobbyManager.getLocalHub().getHubWorld(player);

        if (hubWorld == null || this.isSameHub(status, hubWorld.getHubId()))
        {
            // gracz nie byl na zadnym hubie lub dalej jest na tym samym (tylko na innym serwerze)
            return;
        }

        final ChatPlayer chatPlayer = this.chatManager.getPlayer(identity);
        /*if (! chatPlayer.isOnline()) // teoretycznie zbedne po ostatnich zmianach
        {
            // gdy gracz wychodzi z sieci to tu będzie false
            // unikamy wyjątku PlayerOfflineException
            return;
        }*/

        chatPlayer.leaveRoom(hubWorld.getChatRoom(), true);
    }

    private boolean isSameHub(final IPlayerStatus status, final String hubId)
    {
        if (status.getType() != IPlayerStatus.StatusType.HUB)
        {
            return false;
        }

        final InHubStatus inHubStatus = (InHubStatus) status;
        return hubId.equals(inHubStatus.getHubId());
    }

    /*private void delayRoomJoin(final ChatPlayer player, final ChatRoom room)
    {
        this.bukkitExecutor.asyncLater(RACE_CONDITION_WAIT, () ->
        {
            if (player.isOnline())
            {
                player.joinRoom(room);
            }
        });
    }*/

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
