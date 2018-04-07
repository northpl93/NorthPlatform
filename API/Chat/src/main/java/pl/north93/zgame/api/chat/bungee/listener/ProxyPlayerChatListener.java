package pl.north93.zgame.api.chat.bungee.listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.north93.zgame.api.bungee.BungeeApiCore;
import pl.north93.zgame.api.bungee.proxy.event.HandlePlayerProxyQuitEvent;
import pl.north93.zgame.api.chat.global.ChatManager;
import pl.north93.zgame.api.chat.global.ChatPlayer;
import pl.north93.zgame.api.chat.global.ChatRoom;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.Identity;

public class ProxyPlayerChatListener implements Listener
{
    @Inject
    private ChatManager chatManager;

    @Bean
    private ProxyPlayerChatListener(final BungeeApiCore apiCore)
    {
        apiCore.registerListeners(this);
    }

    @EventHandler
    public void removeQuitingPlayerFromChatRooms(final HandlePlayerProxyQuitEvent event)
    {
        final Identity identity = Identity.of(event.getProxiedPlayer());
        final ChatPlayer player = this.chatManager.getPlayer(identity);

        for (final ChatRoom chatRoom : player.getChatRooms())
        {
            player.leaveRoom(chatRoom);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
