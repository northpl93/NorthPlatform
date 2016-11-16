package pl.north93.zgame.api.global.deployment;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComment;

import pl.north93.zgame.api.global.network.JoiningPolicy;

/**
 * Reprezentuje grupę serwerów mogących pracować na różnych demonach.
 */
public class ServersGroup
{
    @CfgComment("Nazwa tej grupy serwerów")
    private String                  name;
    @CfgComment("Nazwa wzoru według którego mają być tworzone instancje serwerów dla tej grupy")
    private String                  serverPattern;
    private AllocationConfiguration allocatorConfiguration;
    @CfgComment("Uprawnienia dostępu do tej grupy serwerów")
    private JoiningPolicy           joiningPolicy;
    @CfgComment("Minimalna ilość serwerów")
    private Integer                 minServers;
    @CfgComment("Maksymalna ilość serwerów")
    private Integer                 maxServers;

    public String getName()
    {
        return this.name;
    }

    public void setName(final String name)
    {
        this.name = name;
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

    public JoiningPolicy getJoiningPolicy()
    {
        return this.joiningPolicy;
    }

    public void setJoiningPolicy(final JoiningPolicy joiningPolicy)
    {
        this.joiningPolicy = joiningPolicy;
    }

    public Integer getMinServers()
    {
        return this.minServers;
    }

    public void setMinServers(final Integer minServers)
    {
        this.minServers = minServers;
    }

    public Integer getMaxServers()
    {
        return this.maxServers;
    }

    public void setMaxServers(final Integer maxServers)
    {
        this.maxServers = maxServers;
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

        final ServersGroup that = (ServersGroup) o;

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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("serverPattern", this.serverPattern).append("allocatorConfiguration", this.allocatorConfiguration).append("joiningPolicy", this.joiningPolicy).append("minServers", this.minServers).append("maxServers", this.maxServers).toString();
    }
}
