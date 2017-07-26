package pl.north93.zgame.api.global.deployment.serversgroup;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ServersGroupsContainer
{
    private List<IServersGroup> serversGroups;

    public ServersGroupsContainer()
    {
    }

    public ServersGroupsContainer(final List<IServersGroup> serversGroups)
    {
        this.serversGroups = serversGroups;
    }

    public List<IServersGroup> getServersGroups()
    {
        return this.serversGroups;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serversGroups", this.serversGroups).toString();
    }
}
