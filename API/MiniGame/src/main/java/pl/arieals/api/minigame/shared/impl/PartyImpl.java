package pl.arieals.api.minigame.shared.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import pl.arieals.api.minigame.shared.api.location.INetworkLocation;
import pl.arieals.api.minigame.shared.api.party.IParty;

public class PartyImpl implements IParty
{
    private UUID uuid;
    private UUID ownerUuid;
    private Set<UUID> players;
    private INetworkLocation location;

    public PartyImpl()
    {
    }

    public PartyImpl(final UUID ownerUuid, final INetworkLocation location)
    {
        this.uuid = UUID.randomUUID();
        this.ownerUuid = ownerUuid;
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

    @Override
    public Set<UUID> getPlayers()
    {
        return Collections.unmodifiableSet(this.players);
    }

    public void addPlayer(final UUID player)
    {
        this.players.add(player);
    }

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
