package pl.north93.northplatform.api.minigame.shared.api.party.event;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.shared.impl.party.PartyDataImpl;

public class LeavePartyNetEvent extends PartyNetEvent
{
    private UUID playerId;
    private LeavePartyReason reason;

    public LeavePartyNetEvent()
    {
    }

    public LeavePartyNetEvent(final PartyDataImpl party, final UUID playerId, final LeavePartyReason reason)
    {
        super(party);
        this.playerId = playerId;
        this.reason = reason;
    }

    public UUID getPlayerId()
    {
        return this.playerId;
    }

    public LeavePartyReason getReason()
    {
        return this.reason;
    }

    public enum LeavePartyReason
    {
        /**
         * Gracz sam opuscil party.
         */
        SELF,
        /**
         * Gracz zostal usuniety z party przez lidera.
         */
        KICK,
        /**
         * Gracz wyszedl z sieci.
         */
        NETWORK_DISCONNECT
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("playerId", this.playerId).append("reason", this.reason).toString();
    }
}
