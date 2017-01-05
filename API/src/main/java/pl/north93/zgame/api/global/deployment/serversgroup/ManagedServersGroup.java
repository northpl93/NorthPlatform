package pl.north93.zgame.api.global.deployment.serversgroup;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComment;

import pl.north93.zgame.api.global.deployment.AllocationConfiguration;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.server.ServerType;

/**
 * Reprezentuje grupę serwerów mogących pracować na różnych demonach.
 */
public class ManagedServersGroup implements IServersGroup
{
    @CfgComment("Nazwa tej grupy serwerów")
    private String                  name;
    @CfgComment("Typ serwerów tworzonych w tej grupie")
    private ServerType              serversType;
    @CfgComment("Nazwa wzoru według którego mają być tworzone instancje serwerów dla tej grupy")
    private String                  serverPattern;
    private AllocationConfiguration allocatorConfiguration;
    @CfgComment("Uprawnienia dostępu do tej grupy serwerów")
    private JoiningPolicy           joiningPolicy;

    public ManagedServersGroup()
    {
    }

    public ManagedServersGroup(final String name, final ServerType serversType, final String serverPattern, final AllocationConfiguration allocatorConfiguration, final JoiningPolicy joiningPolicy)
    {
        this.name = name;
        this.serversType = serversType;
        this.serverPattern = serverPattern;
        this.allocatorConfiguration = allocatorConfiguration;
        this.joiningPolicy = joiningPolicy;
    }

    @Override
    public ServersGroupType getType()
    {
        return ServersGroupType.MANAGED;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    @Override
    public ServerType getServersType()
    {
        return this.serversType;
    }

    public void setServersType(final ServerType serversType)
    {
        this.serversType = serversType;
    }

    public String getServerPattern()
    {
        return this.serverPattern;
    }

    public void setServerPattern(final String serverPattern)
    {
        this.serverPattern = serverPattern;
    }

    public AllocationConfiguration getAllocatorConfiguration()
    {
        return this.allocatorConfiguration;
    }

    public void setAllocatorConfiguration(final AllocationConfiguration allocatorConfiguration)
    {
        this.allocatorConfiguration = allocatorConfiguration;
    }

    @Override
    public JoiningPolicy getJoiningPolicy()
    {
        return this.joiningPolicy;
    }

    public void setJoiningPolicy(final JoiningPolicy joiningPolicy)
    {
        this.joiningPolicy = joiningPolicy;
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

        final ManagedServersGroup that = (ManagedServersGroup) o;

        return this.name.equals(that.name);

    }

    @Override
    public int hashCode()
    {
        return this.name.hashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("serversType", this.serversType).append("serverPattern", this.serverPattern).append("allocatorConfiguration", this.allocatorConfiguration).append("joiningPolicy", this.joiningPolicy).toString();
    }
}
