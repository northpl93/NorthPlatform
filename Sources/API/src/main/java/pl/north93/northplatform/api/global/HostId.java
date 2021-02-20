package pl.north93.northplatform.api.global;

public final class HostId
{
    private final String hostId;

    public HostId(final String hostId)
    {
        this.hostId = hostId;
    }

    public String getHostId()
    {
        return this.hostId;
    }

    @Override
    public String toString()
    {
        return this.hostId;
    }
}
