package pl.arieals.api.minigame.server.party;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.math.DioriteRandomUtils;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.shared.api.location.INetworkLocation;
import pl.arieals.api.minigame.shared.api.party.IParty;
import pl.arieals.api.minigame.shared.api.party.IPartyManager;
import pl.arieals.api.minigame.shared.api.party.PartyInvite;
import pl.arieals.api.minigame.shared.api.party.PlayerAlreadyHasPartyException;
import pl.arieals.api.minigame.shared.api.party.event.LeavePartyNetEvent.LeavePartyReason;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.Identity;

public class PartyClient
{
    @Inject
    private INetworkManager networkManager;
    @Inject
    private IPartyManager   partyManager;
    @Inject
    private MiniGameServer  miniGameServer;

    @Bean
    private PartyClient()
    {
    }

    public IParty getPlayerParty(final Player player)
    {
        return this.partyManager.getPartyByPlayer(Identity.of(player));
    }

    public PartyInvite getPlayerInvite(final Player player)
    {
        return this.partyManager.getLatestInvite(Identity.of(player));
    }

    public ClientResponse invite(final Player inviter, final String invited)
    {
        final IParty party = this.createPartyIfNeeded(inviter);
        if (party == null)
        {
            return ClientResponse.ERROR;
        }
        else if (! party.isOwner(inviter.getUniqueId()))
        {
            return ClientResponse.NO_OWNER;
        }

        return this.partyManager.access(party.getId(), partyAccess ->
        {
            try
            {
                partyAccess.invitePlayer(Identity.create(null, invited, null));
            }
            catch (final PlayerNotFoundException e)
            {
                return ClientResponse.NO_PLAYER;
            }
            catch (final PlayerAlreadyHasPartyException e)
            {
                return ClientResponse.ALREADY_IN_PARTY;
            }

            return ClientResponse.OK;
        });
    }

    public ClientResponse accept(final Player player)
    {
        final PartyInvite invite = this.getPlayerInvite(player);
        if (invite == null)
        {
            return ClientResponse.NO_INVITE;
        }

        return this.partyManager.access(invite.getPartyId(), partyAccess ->
        {
            try
            {
                partyAccess.addPlayer(Identity.of(player));
            }
            catch (final PlayerAlreadyHasPartyException e)
            {
                return ClientResponse.ALREADY_IN_PARTY;
            }

            return ClientResponse.OK;
        });
    }

    public ClientResponse leave(final Player player)
    {
        final Identity identity = Identity.of(player);

        final IParty party = this.partyManager.getPartyByPlayer(identity);
        if (party == null)
        {
            return ClientResponse.NO_PARTY;
        }

        return this.partyManager.access(party.getId(), partyAccess ->
        {
            if (partyAccess.isOwner(player.getUniqueId()))
            {
                final Set<UUID> players = party.getPlayers();
                if (players.size() == 1)
                {
                    partyAccess.removePlayer(identity, LeavePartyReason.SELF);
                    partyAccess.delete();
                }
                else
                {
                    final Set<UUID> newPlayers = new HashSet<>(players);
                    newPlayers.remove(player.getUniqueId());

                    final Identity newOwner = Identity.create(DioriteRandomUtils.getRandom(newPlayers), null, null);
                    partyAccess.changeOwner(newOwner);
                    partyAccess.removePlayer(identity, LeavePartyReason.SELF);
                }
            }
            else
            {
                partyAccess.removePlayer(identity, LeavePartyReason.SELF);
            }

            return ClientResponse.OK;
        });
    }

    public ClientResponse kick(final Player player, final String nick)
    {
        final Identity identity = Identity.of(player);

        final IParty party = this.partyManager.getPartyByPlayer(identity);
        if (party == null)
        {
            return ClientResponse.NO_PARTY;
        }

        if (! party.isOwner(player.getUniqueId()))
        {
            return ClientResponse.NO_OWNER;
        }

        final Identity target;
        try
        {
            final Identity uncompleted = Identity.create(null, nick, null);
            target = this.networkManager.getPlayers().completeIdentity(uncompleted);
        }
        catch (final PlayerNotFoundException ignored)
        {
            return ClientResponse.NO_PLAYER;
        }

        return this.partyManager.access(party.getId(), partyAccess ->
        {
            if (partyAccess.isOwner(target.getUuid()))
            {
                return ClientResponse.ERROR;
            }

            if (partyAccess.removePlayer(target, LeavePartyReason.KICK))
            {
                return ClientResponse.OK;
            }

            return ClientResponse.NO_PLAYER;
        });
    }

    public void changePartyLocation(final IParty party, final INetworkLocation location)
    {
        this.partyManager.access(party.getId(), partyAccess ->
        {
            partyAccess.changeLocation(location);
        });
    }

    private IParty createPartyIfNeeded(final Player player)
    {
        final IParty party = this.getPlayerParty(player);
        if (party != null)
        {
            return party;
        }

        final INetworkLocation location = this.miniGameServer.getServerManager().getLocation(player);
        try
        {
            return this.partyManager.createParty(Identity.of(player), location);
        }
        catch (final PlayerNotFoundException | PlayerAlreadyHasPartyException e)
        {
            return null;
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
