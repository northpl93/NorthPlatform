package pl.north93.zgame.api.global.deployment.serversgroup;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComment;

import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.server.ServerType;

public class UnManagedServersGroup implements IServersGroup
{
    @CfgComment("Nazwa tej grupy serwerów")
    private String                name;
    @CfgComment("Typ serwerów tworzonych w tej grupie")
    private ServerType            serversType;
    @CfgComment("Uprawnienia dostępu do tej grupy serwerów")
    private JoiningPolicy         joiningPolicy;
    @CfgComment("Lista serwerów w tej grupie.")
    private List<UnManagedServer> servers;

    public UnManagedServersGroup()
    {
    }

    public UnManagedServersGroup(final String name, final ServerType serversType, final JoiningPolicy joiningPolicy, final List<UnManagedServer> servers)
    {
        this.name = name;
        this.serversType = serversType;
        this.joiningPolicy = joiningPolicy;
        this.servers = servers;
    }

    @Override
    public ServersGroupType getType()
    {
        return ServersGroupType.UN_MANAGED;
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

    @Override
    public JoiningPolicy getJoiningPolicy()
    {
        return this.joiningPolicy;
    }

    public void setJoiningPolicy(final JoiningPolicy joiningPolicy)
    {
        this.joiningPolicy = joiningPolicy;
    }

    public List<UnManagedServer> getServers()
    {
        return this.servers;
    }

    public void setServers(final List<UnManagedServer> servers)
    {
        this.servers = servers;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("serversType", this.serversType).append("joiningPolicy", this.joiningPolicy).append("servers", this.servers).toString();
    }
}
