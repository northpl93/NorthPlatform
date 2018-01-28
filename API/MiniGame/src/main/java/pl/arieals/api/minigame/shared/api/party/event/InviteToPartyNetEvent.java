package pl.arieals.api.minigame.shared.api.party.event;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.impl.party.PartyDataImpl;

public class InviteToPartyNetEvent extends PartyNetEvent
{
    private UUID playerId;

    public InviteToPartyNetEvent()
    {
    }

    public InviteToPartyNetEvent(final PartyDataImpl party, final UUID playerId)
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
