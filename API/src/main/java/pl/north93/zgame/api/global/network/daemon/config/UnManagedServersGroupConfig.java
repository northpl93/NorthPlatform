package pl.north93.zgame.api.global.network.daemon.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.server.group.ServersGroupType;

@XmlRootElement(name = "unmanaged")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnManagedServersGroupConfig extends ServersGroupConfig
{
    @XmlElement(name = "server")
    private List<UnManagedServer> servers;

    public UnManagedServersGroupConfig()
    {
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
    public ServersGroupType getType()
    {
        return ServersGroupType.UN_MANAGED;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("servers", this.servers).toString();
    }
}