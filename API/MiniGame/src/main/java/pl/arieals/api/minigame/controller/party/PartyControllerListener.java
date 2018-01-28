package pl.arieals.api.minigame.controller.party;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.diorite.utils.math.DioriteRandomUtils;

import pl.arieals.api.minigame.shared.api.party.IParty;
import pl.arieals.api.minigame.shared.api.party.IPartyManager;
import pl.arieals.api.minigame.shared.api.party.event.InviteToPartyNetEvent;
import pl.arieals.api.minigame.shared.api.party.event.JoinPartyNetEvent;
import pl.arieals.api.minigame.shared.api.party.event.LeavePartyNetEvent.LeavePartyReason;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.event.PlayerQuitNetEvent;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.redis.event.NetEventSubscriber;

public class PartyControllerListener
{
    @Inject
    private INetworkManager networkManager;
    @Inject
    private IPartyManager   partyManager;
    @Inject @Messages("Party")
    private MessagesBox     partyMessages;

    @Bean
    private PartyControllerListener()
    {
    }

    @NetEventSubscriber(InviteToPartyNetEvent.class)
    public void onPartyInvited(final InviteToPartyNetEvent event)
    {
        this.networkManager.getPlayers().ifOnline(event.getPlayerId(), player ->
        {
            final IParty party = event.getParty();

            this.partyMessages.sendMessage(player, "invite.info", this.uuidToNick(party.getOwnerId()));
            this.partyMessages.sendMessage(player, "invite.click");
        });
    }

    @NetEventSubscriber(JoinPartyNetEvent.class)
    public void onPartyJoined(final JoinPartyNetEvent event)
    {
        this.networkManager.getPlayers().ifOnline(event.getPlayerId(), player ->
        {
            final IParty party = event.getParty();

            this.partyMessages.sendMessage(player, "join");

            for (final UUID uuid : party.getPlayers())
            {
                final String messageKey = party.isOwner(uuid) ? "list.leader" : "list.player";
                this.partyMessages.sendMessage(player, messageKey, this.uuidToNick(uuid));
            }
        });
    }

    @NetEventSubscriber(PlayerQuitNetEvent.class)
    public void onPartyMemberQuitNetwork(final PlayerQuitNetEvent event) throws Exception
    {
        final IPlayer player = event.getPlayer();

        // uzyskujemy dostep do party gracza, jesli je posiada
        this.partyManager.access(player, partyAccess ->
        {
            if (partyAccess.isOwner(player.getUuid()))
            {
                final Set<UUID> players = partyAccess.getPlayers();
                if (players.size() == 1)
                {
                    // usuwamy tego gracza z party
                    partyAccess.removePlayer(player.getIdentity(), LeavePartyReason.NETWORK_DISCONNECT);

                    // usuwamy party bez graczy
                    partyAccess.delete();
                }
                else
                {
                    final HashSet<UUID> remainderPlayers = new HashSet<>(players);
                    remainderPlayers.remove(player.getUuid());

                    final Identity newOwner = Identity.create(DioriteRandomUtils.getRandom(remainderPlayers), null, null);
                    partyAccess.changeOwner(newOwner);

                    // usuwamy gracza z party który juz nie jest wlascicielem
                    partyAccess.removePlayer(player.getIdentity(), LeavePartyReason.NETWORK_DISCONNECT);
                }
            }
            else
            {
                partyAccess.removePlayer(player.getIdentity(), LeavePartyReason.NETWORK_DISCONNECT);
            }
        });
    }

    private String uuidToNick(final UUID uuid)
    {
        return this.networkManager.getPlayers().getNickFromUuid(uuid).orElse(uuid.toString());
    }
}
