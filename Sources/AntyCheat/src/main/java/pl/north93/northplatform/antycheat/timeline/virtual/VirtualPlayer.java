package pl.north93.northplatform.antycheat.timeline.virtual;

import pl.north93.northplatform.antycheat.timeline.DataKey;
import pl.north93.northplatform.antycheat.timeline.PlayerData;
import pl.north93.northplatform.antycheat.utils.location.RichEntityLocation;

public interface VirtualPlayer
{
    DataKey<VirtualPlayer> KEY = new DataKey<>("virtualPlayer", VirtualPlayerImpl::new);

    static VirtualPlayer get(final PlayerData playerData)
    {
        return playerData.get(KEY);
    }

    RichEntityLocation getLocation();

    boolean isSprinting();

    //boolean isFlying();

    //boolean isGliding();
}
