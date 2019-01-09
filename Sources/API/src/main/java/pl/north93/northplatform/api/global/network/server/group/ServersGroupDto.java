package pl.north93.northplatform.api.global.network.server.group;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.network.JoiningPolicy;
import pl.north93.northplatform.api.global.network.server.ServerType;

/**
 * Obiekt przechowujacy dane o grupie serwerow.
 * Sluzy do transferu danych przez Redisa.
 */
public class ServersGroupDto implements IServersGroup
{
    private String           name;
    private ServersGroupType type;
    private ServerType       serversType;
    private JoiningPolicy    joiningPolicy;

    public ServersGroupDto()
    {
    }

    public ServersGroupDto(final String name, final ServersGroupType type, final ServerType serversType, final JoiningPolicy joiningPolicy)
    {
        this.name = name;
        this.type = type;
        this.serversType = serversType;
        this.joiningPolicy = joiningPolicy;
    }

    @Override
    public ServersGroupType getType()
    {
        return this.type;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public ServerType getServersType()
    {
        return this.serversType;
    }

    @Override
    public JoiningPolicy getJoiningPolicy()
    {
        return this.joiningPolicy;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("type", this.type).append("serversType", this.serversType).append("joiningPolicy", this.joiningPolicy).toString();
    }
}
