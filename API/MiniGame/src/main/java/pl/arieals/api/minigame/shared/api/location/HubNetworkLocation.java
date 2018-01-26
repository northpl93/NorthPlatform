package pl.arieals.api.minigame.shared.api.location;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Reprezentuje lokację w której znajduje się gracz
 */
public final class HubNetworkLocation implements INetworkLocation
{
    private UUID   serverId;
    private String hubId;

    public HubNetworkLocation()
    {
    }

    public HubNetworkLocation(final UUID serverId, final String hubId)
    {
        this.serverId = serverId;
        this.hubId = hubId;
    }

    @Override
    public UUID getServerId()
    {
        return this.serverId;
    }

    @Override
    public LocationType getType()
    {
        return LocationType.HUB;
    }

    public String getHubId()
    {
        return this.hubId;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serverId", this.serverId).append("hubId", this.hubId).toString();
    }
}
