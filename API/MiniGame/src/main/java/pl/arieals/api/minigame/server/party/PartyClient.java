package pl.arieals.api.minigame.server.party;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.shared.api.location.INetworkLocation;
import pl.arieals.api.minigame.shared.api.party.IParty;
import pl.arieals.api.minigame.shared.api.party.IPartyManager;
import pl.arieals.api.minigame.shared.api.party.PartyInvite;
import pl.arieals.api.minigame.shared.api.party.PlayerAlreadyHasPartyException;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;
import pl.north93.zgame.api.global.network.players.Identity;

public class PartyClient
{
    @Inject
    private IPartyManager  partyManager;
    @Inject
    private MiniGameServer miniGameServer;

    @Bean
    private PartyClient()
    {
    }

    public IParty getPlayerParty(final Player player)
    {
        try
        {
            return this.partyManager.getPartyByPlayer(Identity.of(player));
        }
        catch (final PlayerNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    public PartyInvite getPlayerInvite(final Player player)
    {
        try
        {
            return this.partyManager.getInvite(Identity.of(player));
        }
        catch (final PlayerNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
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

        try
        {
            this.partyManager.invitePlayer(party.getId(), Identity.create(null, invited, null));
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
    }

    public ClientResponse accept(final Player player)
    {
        final PartyInvite invite = this.getPlayerInvite(player);
        if (invite == null)
        {
            return ClientResponse.NO_INVITE;
        }

        try
        {
            this.partyManager.addPlayerToParty(invite.getPartyId(), Identity.of(player));
        }
        catch (final PlayerNotFoundException e)
        {
            e.printStackTrace();
            return ClientResponse.ERROR;
        }
        catch (final PlayerAlreadyHasPartyException e)
        {
            return ClientResponse.ALREADY_IN_PARTY;
        }

        return ClientResponse.OK;
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
}
