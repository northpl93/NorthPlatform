package pl.north93.northplatform.api.minigame.shared.api.party.event;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.shared.impl.party.PartyDataImpl;

public class JoinPartyNetEvent extends PartyNetEvent
{
    private UUID playerId;

    public JoinPartyNetEvent()
    {
    }

    public JoinPartyNetEvent(final PartyDataImpl party, final UUID playerId)
    {
        super(party);
        this.playerId = playerId;
    }

    public UUID getPlayerId()
    {
        return this.playerId;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("playerId", this.playerId).toString();
    }
}
