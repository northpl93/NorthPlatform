package pl.north93.northplatform.api.minigame.controller.party;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.math.DioriteRandomUtils;

import pl.north93.northplatform.api.minigame.shared.api.party.IPartyManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.network.INetworkManager;
import pl.north93.northplatform.api.global.network.event.PlayerQuitNetEvent;
import pl.north93.northplatform.api.global.network.players.IPlayer;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.global.redis.event.NetEventSubscriber;
import pl.north93.northplatform.api.minigame.shared.api.party.event.LeavePartyNetEvent;

/**
 * Listenery odpowiedzialne za działanie Party po stronie kontrolera sieci.
 */
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

    @NetEventSubscriber(PlayerQuitNetEvent.class)
    public void onPartyMemberQuitNetwork(final PlayerQuitNetEvent event) throws Exception
    {
        final IPlayer player = event.getPlayer();

        // uzyskujemy dostep do party gracza, jesli je posiada
        this.partyManager.access(player, partyAccess ->
        {
            if (partyAccess.isOwner(player.getUuid()))
            {
                final Set<Identity> players = partyAccess.getPlayers();
                if (players.size() == 1)
                {
                    // usuwamy tego gracza z party
                    partyAccess.removePlayer(player.getIdentity(), LeavePartyNetEvent.LeavePartyReason.NETWORK_DISCONNECT);

                    // usuwamy party bez graczy
                    partyAccess.delete();
                }
                else
                {
                    final HashSet<Identity> remainderPlayers = new HashSet<>(players);
                    remainderPlayers.remove(player.getIdentity());

                    // zmieniamy ownera party na losowego pozostalego gracza
                    partyAccess.changeOwner(DioriteRandomUtils.getRandom(remainderPlayers));

                    // usuwamy gracza z party który juz nie jest wlascicielem
                    partyAccess.removePlayer(player.getIdentity(), LeavePartyNetEvent.LeavePartyReason.NETWORK_DISCONNECT);
                }
            }
            else
            {
                partyAccess.removePlayer(player.getIdentity(), LeavePartyNetEvent.LeavePartyReason.NETWORK_DISCONNECT);
            }
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
