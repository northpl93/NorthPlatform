package pl.north93.zgame.api.chat.bukkit.engine;

import java.util.UUID;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.chat.global.ChatPlayer;
import pl.north93.zgame.api.chat.global.ChatRoom;
import pl.north93.zgame.api.chat.global.impl.ChatManagerImpl;
import pl.north93.zgame.api.chat.global.impl.data.PlayerChatMessage;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.redis.event.IEventManager;

public class ChatEngine
{
    @Inject
    private BukkitApiCore   apiCore;
    @Inject
    private IEventManager   eventManager;
    @Inject
    private ChatManagerImpl chatManager;

    @Bean
    private ChatEngine()
    {
    }

    public SendMessageResult sendMessageByPlayer(final Player player, final String rawMessage)
    {
        final INorthPlayer northPlayer = INorthPlayer.wrap(player);
        final Identity identity = northPlayer.getIdentity();

        final ChatPlayer chatPlayer = this.chatManager.getPlayer(identity);
        final ChatRoom mainRoom = chatPlayer.getMainRoom();

        if (mainRoom == null)
        {
            return SendMessageResult.NO_ROOM;
        }

        final UUID serverId = this.apiCore.getServerId();

        final BaseComponent format = mainRoom.getChatFormatter().format(northPlayer, rawMessage);
        final String jsonMessage = ComponentSerializer.toString(format);

        final PlayerChatMessage chatMessage = new PlayerChatMessage(mainRoom.getId(), jsonMessage, identity, serverId);
        this.eventManager.callEvent(chatMessage);

        return SendMessageResult.OK;
    }
}
