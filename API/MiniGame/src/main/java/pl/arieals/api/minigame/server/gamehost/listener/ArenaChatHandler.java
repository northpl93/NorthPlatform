package pl.arieals.api.minigame.server.gamehost.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.PlayersManager;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.SpectatorJoinEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.SpectatorModeChangeEvent;
import pl.arieals.api.minigame.shared.api.PlayerStatus;
import pl.north93.zgame.api.chat.global.ChatManager;
import pl.north93.zgame.api.chat.global.ChatPlayer;
import pl.north93.zgame.api.chat.global.ChatRoom;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.Identity;

public class ArenaChatHandler implements Listener
{
    @Inject
    private ChatManager chatManager;

    @EventHandler
    public void addPlayerToChatWhenJoinArena(final PlayerJoinArenaEvent event)
    {
        final ChatPlayer chatPlayer = this.chatManager.getPlayer(Identity.of(event.getPlayer()));
        final PlayersManager playersManager = event.getArena().getPlayersManager();

        final ChatRoom chatRoom = playersManager.getChatRoom();

        chatPlayer.joinRoom(chatRoom);
    }

    @EventHandler
    public void addSpectatorToChatWhenJoinArena(final SpectatorJoinEvent event)
    {
        final ChatPlayer chatPlayer = this.chatManager.getPlayer(Identity.of(event.getPlayer()));
        final PlayersManager playersManager = event.getArena().getPlayersManager();

        final ChatRoom chatRoom = playersManager.getChatRoom();

        chatPlayer.joinRoom(chatRoom);
    }

    @EventHandler
    public void addOrRemoveSpectatorChatOnModeChange(final SpectatorModeChangeEvent event)
    {
        final PlayerStatus oldStatus = event.getOldStatus();
        final PlayerStatus newStatus = event.getNewStatus();

        if (oldStatus == null)
        {
            return;
        }

        final ChatPlayer chatPlayer = this.chatManager.getPlayer(Identity.of(event.getPlayer()));
        final PlayersManager playersManager = event.getArena().getPlayersManager();

        final ChatRoom spectatorsRoom = playersManager.getSpectatorsRoom();
        if (oldStatus.isSpectator() && newStatus.isPlaying())
        {
            chatPlayer.leaveRoom(spectatorsRoom);
        }
        else if (oldStatus.isPlaying() && newStatus.isSpectator())
        {
            chatPlayer.joinRoom(spectatorsRoom);
        }
    }

    @EventHandler
    public void removePlayerFromChatWhenQuitArena(final PlayerQuitArenaEvent event)
    {
        final ChatPlayer chatPlayer = this.chatManager.getPlayer(Identity.of(event.getPlayer()));
        final PlayersManager playersManager = event.getArena().getPlayersManager();

        final ChatRoom chatRoom = playersManager.getChatRoom();
        final ChatRoom spectatorsRoom = playersManager.getSpectatorsRoom();

        chatPlayer.leaveRoom(chatRoom);
        chatPlayer.leaveRoom(spectatorsRoom);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
