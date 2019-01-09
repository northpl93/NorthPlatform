package pl.north93.northplatform.api.chat.bukkit.engine;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.chat.global.ChatPlayer;
import pl.north93.northplatform.api.chat.global.ChatRoom;
import pl.north93.northplatform.api.chat.global.impl.ChatManagerImpl;
import pl.north93.northplatform.api.chat.global.impl.data.PlayerChatMessage;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

@Slf4j
public class ChatEngine
{
    @Inject
    private BukkitApiCore   apiCore;
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
        final ChatRoom mainRoom = chatPlayer.getActiveRoom();

        if (mainRoom == null)
        {
            return SendMessageResult.NO_ROOM;
        }

        final String processedRawMessage = this.processRawMessage(player, rawMessage);
        log.info("[{}] {}: {}", mainRoom.getId(), player.getName(), processedRawMessage);

        final BaseComponent message = mainRoom.getChatFormatter().format(northPlayer, processedRawMessage);
        if (this.processIfOnlyLocal(mainRoom, message))
        {
            // wszyscy gracze z pokoju byli online na lokalnym serwerze; wiec nie angazujemy
            // redisa w wysylanie wiadomosci
            return SendMessageResult.OK;
        }
        else
        {
            // używamy redisa do rozgłoszenia wiadomości
            this.processRemote(mainRoom, identity, message);
            return SendMessageResult.OK;
        }
    }

    // wykonuje rozne operacja na surowym tekscie
    private String processRawMessage(final Player player, final String rawMessage)
    {
        if (player.hasPermission("chat.colorize"))
        {
            return rawMessage;
        }

        return StringUtils.remove(rawMessage, '&');
    }

    // sprawdza czy na lokalnym serwerze sa wszyscy gracze w pokoju
    // jak tak to wysyla do nich wiadomosc i zwraca true
    private boolean processIfOnlyLocal(final ChatRoom chatRoom, final BaseComponent message)
    {
        final Collection<Identity> participants = chatRoom.getParticipants();

        final Function<Identity, Player> mapper = identity -> Bukkit.getPlayerExact(identity.getNick());
        final List<Player> localPlayers = participants.stream().map(mapper).filter(Objects::nonNull).collect(Collectors.toList());

        if (participants.size() != localPlayers.size())
        {
            return false;
        }

        localPlayers.forEach(player -> player.sendMessage(message));
        return true;
    }

    // wysyła wiadomość do redisa
    private void processRemote(final ChatRoom mainRoom, final Identity sender, final BaseComponent message)
    {
        final String jsonMessage = ComponentSerializer.toString(message);
        final UUID serverId = this.apiCore.getServerId();

        final PlayerChatMessage chatMessage = new PlayerChatMessage(mainRoom.getId(), jsonMessage, sender, serverId);
        this.chatManager.sendMessage(chatMessage);
    }
}
