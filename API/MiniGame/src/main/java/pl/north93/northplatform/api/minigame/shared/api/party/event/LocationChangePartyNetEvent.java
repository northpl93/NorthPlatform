package pl.north93.northplatform.api.minigame.shared.api.party.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.shared.api.status.IPlayerStatus;
import pl.north93.northplatform.api.minigame.shared.impl.party.PartyDataImpl;

public class LocationChangePartyNetEvent extends PartyNetEvent
{
    private IPlayerStatus location;

    public LocationChangePartyNetEvent()
    {
    }

    public LocationChangePartyNetEvent(final PartyDataImpl party, final IPlayerStatus location)
    {
        super(party);
        this.location = location;
    }

    public IPlayerStatus getLocation()
    {
        return this.location;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("location", this.location).toString();
    }
}
