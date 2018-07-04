package pl.north93.zgame.api.chat.bungee.listener;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.Chat;
import pl.north93.zgame.api.chat.global.ChatRoom;
import pl.north93.zgame.api.chat.global.impl.ChatManagerImpl;
import pl.north93.zgame.api.chat.global.impl.data.AbstractChatData;
import pl.north93.zgame.api.chat.global.impl.data.BroadcastMessage;
import pl.north93.zgame.api.chat.global.impl.data.PlayerChatMessage;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.redis.event.NetEventSubscriber;

/**
 * Klasa odpowiedzialna za rozsyłanie wiadomości do graczy połączonych z daną instancją proxy.
 */
public class MessageHandler
{
    @Inject
    private Logger          logger;
    @Inject
    private INetworkManager networkManager;
    @Inject
    private ChatManagerImpl chatManager;

    @Bean
    private MessageHandler()
    {
    }

    @NetEventSubscriber(PlayerChatMessage.class)
    public void handleMessage(final PlayerChatMessage chatMessage)
    {
        final Collection<Identity> participants = this.getParticipants(chatMessage);

        final ProxyServer proxyServer = ProxyServer.getInstance();
        final Chat chatPacket = new Chat(chatMessage.getMessage());

        for (final Identity participant : participants)
        {
            final ProxiedPlayer proxyPlayer = proxyServer.getPlayer(participant.getUuid());
            if (proxyPlayer == null)
            {
                // to zupełnie normalna sytuacja w przypadku setupu z wieloma bungee
                continue;
            }

            proxyPlayer.unsafe().sendPacket(chatPacket);
        }
    }

    @NetEventSubscriber(BroadcastMessage.class)
    public void handleBroadcast(final BroadcastMessage message)
    {
        final Collection<Identity> participants = this.getParticipants(message);

        final ProxyServer proxyServer = ProxyServer.getInstance();
        final Chat chatPacket = new Chat(message.getMessage());

        for (final Identity participant : participants)
        {
            final ProxiedPlayer proxyPlayer = proxyServer.getPlayer(participant.getUuid());
            if (proxyPlayer == null || this.shouldSkipSending(message, participant))
            {
                // to zupełnie normalna sytuacja w przypadku setupu z wieloma bungee
                continue;
            }

            proxyPlayer.unsafe().sendPacket(chatPacket);
        }
    }

    private boolean shouldSkipSending(final BroadcastMessage broadcastMessage, final Identity participant)
    {
        if (broadcastMessage.getLocale() == null)
        {
            return false;
        }

        final IOnlinePlayer player = this.networkManager.getPlayers().unsafe().getOnlineValue(participant.getNick()).get();
        if (player == null)
        {
            return true;
        }

        final Locale locale = player.getMyLocale();
        return ! locale.toLanguageTag().equals(broadcastMessage.getLocale());
    }

    private Collection<Identity> getParticipants(final AbstractChatData message)
    {
        final String roomId = message.getRoomId();

        final ChatRoom room = this.chatManager.getRoom(roomId);
        if (room == null)
        {
            this.logger.log(Level.WARNING, "Received chat data with invalid roomId {0}", roomId);
            return Collections.emptyList();
        }

        return room.getParticipants();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
