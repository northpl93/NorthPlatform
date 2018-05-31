package pl.arieals.api.minigame.shared.api.status;

import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Reprezentuje lokację w której znajduje się gracz
 */
public final class InHubStatus implements IPlayerStatus
{
    private UUID   serverId;
    private String hubId;

    public InHubStatus()
    {
    }

    public InHubStatus(final UUID serverId, final String hubId)
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
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }
        final InHubStatus that = (InHubStatus) o;
        return Objects.equals(this.serverId, that.serverId) && Objects.equals(this.hubId, that.hubId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.serverId, this.hubId);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serverId", this.serverId).append("hubId", this.hubId).toString();
    }
}
