package pl.arieals.api.minigame.shared.api.hub;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RemoteHub implements IHubServer
{
    private UUID serverId;

    public RemoteHub()
    {
    }

    public RemoteHub(final UUID serverId)
    {
        this.serverId = serverId;
    }

    public RemoteHub(final IHubServer other)
    {
        this.serverId = other.getServerId();
    }

    @Override
    public UUID getServerId()
    {
        return this.serverId;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serverId", this.serverId).toString();
    }
}
