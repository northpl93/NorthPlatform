package pl.north93.zgame.controller.servers.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.server.group.ServersGroupType;
import pl.north93.zgame.controller.servers.groups.ILocalServersGroup;

/**
 * Reprezentuje grupę serwerów mogących pracować na różnych demonach.
 */
@XmlRootElement(name = "managed")
@XmlAccessorType(XmlAccessType.FIELD)
public class ManagedServersGroupConfig extends ServersGroupConfig
{
    @XmlElement
    private String serverPattern;

    public ManagedServersGroupConfig()
    {
    }

    public String getServerPattern()
    {
        return this.serverPattern;
    }

    public void setServerPattern(final String serverPattern)
    {
        this.serverPattern = serverPattern;
    }

    @Override
    public ServersGroupType getType()
    {
        return ServersGroupType.MANAGED;
    }

    @Override
    public ILocalServersGroup createLocalGroup()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serverPattern", this.serverPattern).toString();
    }
}
