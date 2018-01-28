package pl.arieals.api.minigame.shared.impl.party;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import pl.arieals.api.minigame.shared.api.location.INetworkLocation;
import pl.arieals.api.minigame.shared.api.party.IParty;
import pl.arieals.api.minigame.shared.api.party.PartyInvite;

public class PartyDataImpl implements IParty
{
    private UUID uuid;
    private UUID ownerUuid;
    private Set<PartyInvite> invites;
    private Set<UUID>        players;
    private INetworkLocation location;

    public PartyDataImpl()
    {
    }

    public PartyDataImpl(final UUID ownerUuid, final INetworkLocation location)
    {
        this.uuid = UUID.randomUUID();
        this.ownerUuid = ownerUuid;
        this.invites = new HashSet<>(4);
        this.players = new HashSet<>(4);
        this.players.add(ownerUuid);
        this.location = location;
    }

    @Override
    public UUID getId()
    {
        return this.uuid;
    }

    @Override
    public UUID getOwnerId()
    {
        return this.ownerUuid;
    }

    public void setOwnerUuid(final UUID ownerUuid)
    {
        this.ownerUuid = ownerUuid;
    }

    @Override
    public Set<PartyInvite> getInvites()
    {
        this.invites.removeIf(PartyInvite::isExpired);
        return Collections.unmodifiableSet(this.invites);
    }

    @Override
    public boolean isInvited(final UUID playerId)
    {
        for (final PartyInvite invite : this.invites)
        {
            if (invite.getPlayerId().equals(playerId) && invite.isStillValid())
            {
                return true;
            }
        }
        return false;
    }

    public void addInvite(final PartyInvite invite)
    {
        this.invites.add(invite);
    }

    public void removeInvite(final PartyInvite invite)
    {
        this.invites.remove(invite);
    }

    public void removeInviteOfPlayer(final UUID player)
    {
        this.invites.removeIf(invite -> invite.getPlayerId().equals(player));
    }

    @Override
    public Set<UUID> getPlayers()
    {
        return Collections.unmodifiableSet(this.players);
    }

    @Override
    public boolean isAddedOrInvited(final UUID playerId)
    {
        return this.players.contains(playerId) || this.isInvited(playerId);
    }

    public void addPlayer(final UUID player)
    {
        this.players.add(player);
        this.removeInviteOfPlayer(player);
    }

    public void removePlayer(final UUID player)
    {
        this.players.remove(player);
    }

    @Override
    public boolean isAdded(final UUID playerId)
    {
        return this.players.contains(playerId);
    }

    @Override
    public INetworkLocation getTargetLocation()
    {
        return this.location;
    }

    public void setLocation(final INetworkLocation location)
    {
        this.location = location;
    }
}
