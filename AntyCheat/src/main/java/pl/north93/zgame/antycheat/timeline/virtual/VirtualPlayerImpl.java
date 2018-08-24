package pl.north93.zgame.antycheat.timeline.virtual;

import lombok.ToString;
import pl.north93.zgame.antycheat.utils.location.RichEntityLocation;

@ToString
/*default*/ class VirtualPlayerImpl implements VirtualPlayer
{
    private RichEntityLocation location;
    private boolean            sprinting;

    @Override
    public RichEntityLocation getLocation()
    {
        return this.location;
    }

    public void updateLocation(final RichEntityLocation location)
    {
        this.location = location;
    }

    @Override
    public boolean isSprinting()
    {
        return this.sprinting;
    }

    public void updateSprinting(final boolean sprinting)
    {
        this.sprinting = sprinting;
    }
}
