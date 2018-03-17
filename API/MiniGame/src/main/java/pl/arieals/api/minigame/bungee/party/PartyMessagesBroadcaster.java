package pl.arieals.api.minigame.bungee.party;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import pl.arieals.api.minigame.shared.api.party.IParty;
import pl.arieals.api.minigame.shared.api.party.IPartyManager;
import pl.arieals.api.minigame.shared.api.party.event.InviteToPartyNetEvent;
import pl.arieals.api.minigame.shared.api.party.event.JoinPartyNetEvent;
import pl.north93.zgame.api.bungee.BungeeApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerType;
import pl.north93.zgame.api.global.redis.event.NetEventSubscriber;

/**
 * Klasa w BungeeCordowej części komponentu odpowiedzialna
 * za rozgłaszanie wiadomości dotyczących grup.
 */
public class PartyMessagesBroadcaster implements Listener
{
    @Inject
    private INetworkManager networkManager;
    @Inject
    private IPartyManager   partyManager;
    @Inject @Messages("Party")
    private MessagesBox     partyMessages;

    @Bean
    private PartyMessagesBroadcaster(final BungeeApiCore apiCore)
    {
        apiCore.registerListeners(this);
    }

    @NetEventSubscriber(InviteToPartyNetEvent.class)
    public void onPartyInvited(final InviteToPartyNetEvent event)
    {
        final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(event.getPlayerId());
        if (proxiedPlayer == null)
        {
            return;
        }

        final String serverName = proxiedPlayer.getServer().getInfo().getName();
        final Server server = this.networkManager.getServers().withProxyName(serverName);
        if (server.getType() == ServerType.MINIGAME)
        {
            return;
        }

        final IParty party = event.getParty();

        this.partyMessages.sendMessage(proxiedPlayer, "separator");
        this.partyMessages.sendMessage(proxiedPlayer, "header", MessageLayout.CENTER);
        proxiedPlayer.sendMessage();
        this.partyMessages.sendMessage(proxiedPlayer, "invite.info", MessageLayout.CENTER, this.uuidToNick(party.getOwnerId()));

        final String cmdClickMessage = this.partyMessages.getMessage(proxiedPlayer.getLocale(), "invite.cmd");
        final BaseComponent[] cmdClickComponents = new ComponentBuilder(cmdClickMessage).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept")).create();
        proxiedPlayer.sendMessage(MessageLayout.CENTER.processMessage(new TextComponent(cmdClickComponents)));

        proxiedPlayer.sendMessage();
        this.partyMessages.sendMessage(proxiedPlayer, "separator");
    }

    @NetEventSubscriber(JoinPartyNetEvent.class)
    public void onPartyJoined(final JoinPartyNetEvent event)
    {
        final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(event.getPlayerId());
        if (proxiedPlayer == null)
        {
            return;
        }

        final IParty party = event.getParty();
        this.announceJoinToParty(party, proxiedPlayer);

        this.partyMessages.sendMessage(proxiedPlayer, "separator");
        this.partyMessages.sendMessage(proxiedPlayer, "header", MessageLayout.CENTER);
        proxiedPlayer.sendMessage();
        this.partyMessages.sendMessage(proxiedPlayer, "join.success", MessageLayout.CENTER);

        for (final UUID uuid : party.getPlayers())
        {
            final String messageKey = party.isOwner(uuid) ? "list.leader" : "list.player";
            this.partyMessages.sendMessage(proxiedPlayer, messageKey, MessageLayout.CENTER, this.uuidToNick(uuid));
        }

        proxiedPlayer.sendMessage();
        this.partyMessages.sendMessage(proxiedPlayer, "separator");
    }

    // rozsyła członkom party info o wejściu gracza
    private void announceJoinToParty(final IParty party, final ProxiedPlayer joiningProxiedPlayer)
    {
        for (final UUID uuid : party.getPlayers())
        {
            if (uuid.equals(joiningProxiedPlayer.getUniqueId()))
            {
                continue;
            }

            this.networkManager.getPlayers().ifOnline(uuid, onlinePlayer ->
            {
                this.partyMessages.sendMessage(onlinePlayer, "join.broadcast", joiningProxiedPlayer.getDisplayName());
            });
        }
    }

    private String uuidToNick(final UUID uuid)
    {
        return this.networkManager.getPlayers().getNickFromUuid(uuid).orElse(uuid.toString());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
