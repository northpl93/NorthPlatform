package pl.north93.northplatform.api.global;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
public final class HostId
{
    private final String hostId;

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
