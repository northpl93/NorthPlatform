package pl.arieals.api.minigame.shared.api.party;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class PartyInvite
{
    private UUID     partyId;
    private UUID     playerId;
    private Instant  createdAt;
    private Duration duration;

    public PartyInvite()
    {
    }

    public PartyInvite(final UUID partyId, final UUID playerId, final Instant createdAt, final Duration duration)
    {
        this.partyId = partyId;
        this.playerId = playerId;
        this.createdAt = createdAt;
        this.duration = duration;
    }

    public UUID getPartyId()
    {
        return this.partyId;
    }

    public UUID getPlayerId()
    {
        return this.playerId;
    }

    public Instant getCreatedAt()
    {
        return this.createdAt;
    }

    public boolean isStillValid()
    {
        return this.createdAt.plus(this.duration).isAfter(Instant.now());
    }

    public boolean isExpired()
    {
        return ! this.isStillValid();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("partyId", this.partyId).append("playerId", this.playerId).append("createdAt", this.createdAt).append("duration", this.duration).toString();
    }
}
