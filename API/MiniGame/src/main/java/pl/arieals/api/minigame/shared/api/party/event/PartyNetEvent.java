package pl.arieals.api.minigame.shared.api.party.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.party.IParty;
import pl.arieals.api.minigame.shared.impl.PartyImpl;
import pl.north93.zgame.api.global.redis.event.INetEvent;

public abstract class PartyNetEvent implements INetEvent
{
    private PartyImpl party;

    public PartyNetEvent(final PartyImpl party)
    {
        this.party = party;
    }

    public IParty getParty()
    {
        return this.party;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("party", this.party).toString();
    }
}
