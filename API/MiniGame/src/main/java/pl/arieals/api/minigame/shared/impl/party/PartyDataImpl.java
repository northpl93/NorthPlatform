package pl.arieals.api.minigame.shared.impl.party;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import pl.arieals.api.minigame.shared.api.PlayerJoinInfo;
import pl.arieals.api.minigame.shared.api.party.IParty;
import pl.arieals.api.minigame.shared.api.party.PartyInvite;
import pl.arieals.api.minigame.shared.api.status.IPlayerStatus;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.Identity;

public class PartyDataImpl implements IParty
{
    @Inject
    private static PartyManagerImpl partyManager;

    private UUID             uuid;
    private Identity         owner;
    private Set<PartyInvite> invites;
    private Set<Identity>    players;
    private IPlayerStatus    location;

    public PartyDataImpl()
    {
    }

    public PartyDataImpl(final Identity owner, final IPlayerStatus location)
    {
        this.uuid = UUID.randomUUID();
        this.owner = owner;
        this.invites = new HashSet<>(4);
        this.players = new HashSet<>(4);
        this.players.add(owner);
        this.location = location;
    }

    @Override
    public UUID getId()
    {
        return this.uuid;
    }

    @Override
    public Identity getOwner()
    {
        return this.owner;
    }

    public void setOwner(final Identity owner)
    {
        this.owner = owner;
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
    public Set<Identity> getPlayers()
    {
        return Collections.unmodifiableSet(this.players);
    }

    @Override
    public Set<PlayerJoinInfo> getJoinInfos()
    {
        return partyManager.getJoinInfos(this.players);
    }

    @Override
    public boolean isAddedOrInvited(final UUID playerId)
    {
        return this.isAdded(playerId) || this.isInvited(playerId);
    }

    public void addPlayer(final Identity player)
    {
        this.players.add(player);
        this.removeInviteOfPlayer(player.getUuid());
    }

    public void removePlayer(final Identity player)
    {
        this.players.remove(player);
    }

    @Override
    public boolean isAdded(final UUID playerId)
    {
        for (final Identity identity : this.players)
        {
            if (identity.getUuid().equals(playerId))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public IPlayerStatus getTargetLocation()
    {
        return this.location;
    }

    public void setLocation(final IPlayerStatus location)
    {
        this.location = location;
    }
}
