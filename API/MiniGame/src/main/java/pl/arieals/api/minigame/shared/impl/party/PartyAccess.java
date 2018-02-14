package pl.arieals.api.minigame.shared.impl.party;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.location.INetworkLocation;
import pl.arieals.api.minigame.shared.api.party.IPartyAccess;
import pl.arieals.api.minigame.shared.api.party.PartyInvite;
import pl.arieals.api.minigame.shared.api.party.PlayerAlreadyHasPartyException;
import pl.arieals.api.minigame.shared.api.party.event.InviteToPartyNetEvent;
import pl.arieals.api.minigame.shared.api.party.event.JoinPartyNetEvent;
import pl.arieals.api.minigame.shared.api.party.event.LeavePartyNetEvent;
import pl.arieals.api.minigame.shared.api.party.event.LeavePartyNetEvent.LeavePartyReason;
import pl.arieals.api.minigame.shared.api.party.event.LocationChangePartyNetEvent;
import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.Identity;

/*default*/ class PartyAccess implements IPartyAccess
{
    private final PartyManagerImpl partyManager;
    private final PartyDataImpl    partyData;
    private       boolean          deleted;

    public PartyAccess(final PartyManagerImpl partyManager, final PartyDataImpl partyData)
    {
        this.partyManager = partyManager;
        this.partyData = partyData;
    }

    @Override
    public void changeLocation(final INetworkLocation location)
    {
        Preconditions.checkState(this.isNotDeleted(), "Party is deleted");

        this.partyData.setLocation(location);
        this.partyManager.callNetEvent(new LocationChangePartyNetEvent(this.partyData, location));
    }

    @Override
    public boolean changeOwner(final Identity newOwnerIdentity) throws PlayerNotFoundException
    {
        Preconditions.checkState(this.isNotDeleted(), "Party is deleted");

        try (final IPlayerTransaction t = this.partyManager.openTransaction(newOwnerIdentity))
        {
            final IPlayer player = t.getPlayer();

            if (this.isAdded(player.getUuid()))
            {
                this.partyData.setOwnerUuid(player.getUuid());
                return true;
            }

            return false;
        }
    }

    @Override
    public boolean invitePlayer(final Identity playerIdentity) throws PlayerNotFoundException, PlayerAlreadyHasPartyException
    {
        Preconditions.checkState(this.isNotDeleted(), "Party is deleted");

        try (final IPlayerTransaction t = this.partyManager.openTransaction(playerIdentity))
        {
            final IPlayer player = t.getPlayer();
            if (t.isOffline())
            {
                throw new PlayerNotFoundException(player.getLatestNick());
            }

            if (this.partyManager.playerHasParty(player))
            {
                throw new PlayerAlreadyHasPartyException();
            }

            final PartyInvite latestPlayerInvite = this.partyManager.getLatestInviteFromPlayer(player);
            if (latestPlayerInvite != null && latestPlayerInvite.getPartyId().equals(this.getId()))
            {
                return false;
            }

            final PartyInvite newInvite = new PartyInvite(this.getId(), player.getUuid(), Instant.now(), Duration.ofSeconds(100));

            this.partyManager.setPartyInvite(player, newInvite);
            this.partyData.addInvite(newInvite);

            this.partyManager.callNetEvent(new InviteToPartyNetEvent(this.partyData, player.getUuid()));
            return true;
        }
    }

    @Override
    public boolean revokeInvite(final Identity playerIdentity) throws PlayerNotFoundException
    {
        Preconditions.checkState(this.isNotDeleted(), "Party is deleted");

        try (final IPlayerTransaction t = this.partyManager.openTransaction(playerIdentity))
        {
            final IPlayer player = t.getPlayer();

            if (! this.isInvited(player.getUuid()))
            {
                return false;
            }

            final PartyInvite latestInvite = this.partyManager.getLatestInviteFromPlayer(player);
            if (latestInvite != null && latestInvite.getPartyId().equals(this.getId()))
            {
                // Jesli ostatnie zaproszenie gracza jest do tej grupy to je usuwamy
                this.partyManager.setPartyInvite(player, null);
            }

            this.partyData.removeInviteOfPlayer(player.getUuid());
            return true;
        }
    }

    @Override
    public void addPlayer(final Identity playerIdentity) throws PlayerNotFoundException, PlayerAlreadyHasPartyException
    {
        Preconditions.checkState(this.isNotDeleted(), "Party is deleted");

        try (final IPlayerTransaction t = this.partyManager.openTransaction(playerIdentity))
        {
            final IPlayer player = t.getPlayer();
            if (t.isOffline())
            {
                throw new PlayerNotFoundException(player.getLatestNick());
            }

            if (this.partyManager.playerHasParty(player))
            {
                throw new PlayerAlreadyHasPartyException();
            }

            this.partyManager.setPartyInvite(player, null);
            this.partyManager.setPartyId(player, this.getId());

            this.partyData.addPlayer(player.getUuid());
            this.partyManager.callNetEvent(new JoinPartyNetEvent(this.partyData, player.getUuid()));
        }
    }

    @Override
    public boolean removePlayer(final Identity playerIdentity, final LeavePartyReason reason) throws PlayerNotFoundException
    {
        Preconditions.checkState(this.isNotDeleted(), "Party is deleted");

        try (final IPlayerTransaction t = this.partyManager.openTransaction(playerIdentity))
        {
            final IPlayer player = t.getPlayer();

            if (this.isAdded(player.getUuid()))
            {
                this.partyData.removePlayer(player.getUuid());
                this.partyManager.callNetEvent(new LeavePartyNetEvent(this.partyData, player.getUuid(), reason));
            }
            else
            {
                return false;
            }

            this.partyManager.setPartyId(player, null);
            return true;
        }
    }

    @Override
    public void delete()
    {
        Preconditions.checkState(this.isNotDeleted(), "Party is deleted");

        for (final UUID player : this.getPlayers())
        {
            this.removePlayer(Identity.create(player, null, null), LeavePartyReason.KICK);
        }

        this.deleted = true;
    }

    public boolean isDeleted()
    {
        return this.deleted;
    }

    public boolean isNotDeleted()
    {
        return ! this.deleted;
    }

    @Override
    public UUID getId()
    {
        return this.partyData.getId();
    }

    @Override
    public UUID getOwnerId()
    {
        return this.partyData.getOwnerId();
    }

    @Override
    public Set<PartyInvite> getInvites()
    {
        return this.partyData.getInvites();
    }

    @Override
    public boolean isInvited(final UUID playerId)
    {
        return this.partyData.isInvited(playerId);
    }

    @Override
    public Set<UUID> getPlayers()
    {
        return this.partyData.getPlayers();
    }

    @Override
    public boolean isAdded(final UUID playerId)
    {
        return this.partyData.isAdded(playerId);
    }

    @Override
    public boolean isAddedOrInvited(final UUID playerId)
    {
        return this.partyData.isAddedOrInvited(playerId);
    }

    @Override
    public INetworkLocation getTargetLocation()
    {
        return this.partyData.getTargetLocation();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("partyData", this.partyData).toString();
    }
}
