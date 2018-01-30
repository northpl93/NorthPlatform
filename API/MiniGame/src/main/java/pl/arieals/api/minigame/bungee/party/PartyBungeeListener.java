package pl.arieals.api.minigame.bungee.party;

import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
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

public class PartyBungeeListener implements Listener
{
    @Inject
    private INetworkManager networkManager;
    @Inject
    private IPartyManager   partyManager;
    @Inject @Messages("Party")
    private MessagesBox     partyMessages;

    @Bean
    private PartyBungeeListener(final BungeeApiCore apiCore)
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
        this.partyMessages.sendMessage(proxiedPlayer, "invite.cmd", MessageLayout.CENTER);
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

    private String uuidToNick(final UUID uuid)
    {
        return this.networkManager.getPlayers().getNickFromUuid(uuid).orElse(uuid.toString());
    }
}
