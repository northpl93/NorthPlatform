package pl.north93.northplatform.api.minigame.shared.api.party.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.shared.api.party.IParty;
import pl.north93.northplatform.api.minigame.shared.impl.party.PartyDataImpl;
import pl.north93.northplatform.api.global.redis.event.INetEvent;

public abstract class PartyNetEvent implements INetEvent
{
    private PartyDataImpl party;

    public PartyNetEvent() // serialization
    {
    }

    public PartyNetEvent(final PartyDataImpl party)
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
