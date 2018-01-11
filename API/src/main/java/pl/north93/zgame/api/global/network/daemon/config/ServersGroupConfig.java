package pl.north93.zgame.api.global.network.daemon.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.server.ServerType;
import pl.north93.zgame.api.global.network.server.group.ServersGroupType;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class ServersGroupConfig
{
    @XmlAttribute
    private String        name;
    @XmlElement
    private ServerType    serversType;
    @XmlElement
    private JoiningPolicy joiningPolicy;

    public String getName()
    {
        return this.name;
    }

    public abstract ServersGroupType getType();

    public ServerType getServersType()
    {
        return this.serversType;
    }

    public JoiningPolicy getJoiningPolicy()
    {
        return this.joiningPolicy;
    }

    public void mergeConfigIntoThis(final ServersGroupConfig config)
    {
        this.serversType = config.serversType;
        this.joiningPolicy = config.joiningPolicy;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("serversType", this.serversType).append("joiningPolicy", this.joiningPolicy).toString();
    }
}
