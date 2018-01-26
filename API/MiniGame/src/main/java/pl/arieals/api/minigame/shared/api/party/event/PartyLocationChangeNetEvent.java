package pl.arieals.api.minigame.shared.api.party.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.location.INetworkLocation;
import pl.arieals.api.minigame.shared.impl.PartyImpl;

public class PartyLocationChangeNetEvent extends PartyNetEvent
{
    private INetworkLocation location;

    public PartyLocationChangeNetEvent(final PartyImpl party, final INetworkLocation location)
    {
        super(party);
        this.location = location;
    }

    public INetworkLocation getLocation()
    {
        return this.location;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("location", this.location).toString();
    }
}
