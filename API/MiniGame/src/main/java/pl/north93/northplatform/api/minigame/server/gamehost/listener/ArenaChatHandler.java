package pl.north93.northplatform.api.minigame.server.gamehost.listener;

import org.bukkit.event.EventHandler;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.player.ArenaChatManager;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerArenaEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.SpectatorJoinEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.SpectatorModeChangeEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.SpectatorQuitEvent;
import pl.north93.northplatform.api.minigame.shared.api.PlayerStatus;
import pl.north93.northplatform.api.bukkit.utils.AutoListener;
import pl.north93.northplatform.api.chat.global.ChatManager;
import pl.north93.northplatform.api.chat.global.ChatPlayer;
import pl.north93.northplatform.api.chat.global.ChatRoom;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.players.Identity;

public class ArenaChatHandler implements AutoListener
{
    @Inject
    private ChatManager chatManager;

    @EventHandler
    public void addPlayerToChatWhenJoinArena(final PlayerJoinArenaEvent event)
    {
        final ChatPlayer chatPlayer = this.chatManager.getPlayer(Identity.of(event.getPlayer()));
        final ArenaChatManager chatManager = event.getArena().getChatManager();

        final ChatRoom chatRoom = chatManager.getChatRoom();

        chatPlayer.joinRoom(chatRoom);
    }

    @EventHandler
    public void addSpectatorToChatWhenJoinArena(final SpectatorJoinEvent event)
    {
        final ChatPlayer chatPlayer = this.chatManager.getPlayer(Identity.of(event.getPlayer()));
        final ArenaChatManager chatManager = event.getArena().getChatManager();

        final ChatRoom chatRoom = chatManager.getChatRoom();

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
        final ArenaChatManager chatManager = event.getArena().getChatManager();

        final ChatRoom spectatorsRoom = chatManager.getSpectatorsRoom();
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
        this.removeFromChat(event);
    }

    @EventHandler
    public void removeSpectatorFromChatWhenQuitArena(final SpectatorQuitEvent event)
    {
        this.removeFromChat(event);
    }

    // usuwa gracza z pokojów czatu danej areny
    private void removeFromChat(final PlayerArenaEvent event)
    {
        final ChatPlayer chatPlayer = this.chatManager.getPlayer(Identity.of(event.getPlayer()));
        if (! chatPlayer.isOnline())
        {
            // gracz wyszedl z sieci, nie musimy go usuwac z kanalow
            return;
        }

        final ArenaChatManager chatManager = event.getArena().getChatManager();

        final ChatRoom chatRoom = chatManager.getChatRoom();
        final ChatRoom spectatorsRoom = chatManager.getSpectatorsRoom();

        chatPlayer.leaveRoom(chatRoom, true);
        chatPlayer.leaveRoom(spectatorsRoom, true);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
