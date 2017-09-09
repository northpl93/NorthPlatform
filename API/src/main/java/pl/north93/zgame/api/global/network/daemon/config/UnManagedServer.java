package pl.north93.zgame.api.global.network.daemon.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "server")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnManagedServer
{
    @XmlAttribute
    private UUID    serverId;
    @XmlAttribute
    private String  connectIp;
    @XmlAttribute
    private Integer connectPort;

    public UnManagedServer()
    {
    }

    public UnManagedServer(final UUID serverId, final String connectIp, final Integer connectPort)
    {
        this.serverId = serverId;
        this.connectIp = connectIp;
        this.connectPort = connectPort;
    }

    public UUID getServerId()
    {
        return this.serverId;
    }

    public void setServerId(final UUID serverId)
    {
        this.serverId = serverId;
    }

    public String getConnectIp()
    {
        return this.connectIp;
    }

    public void setConnectIp(final String connectIp)
    {
        this.connectIp = connectIp;
    }

    public Integer getConnectPort()
    {
        return this.connectPort;
    }

    public void setConnectPort(final Integer connectPort)
    {
        this.connectPort = connectPort;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serverId", this.serverId).append("connectIp", this.connectIp).append("connectPort", this.connectPort).toString();
    }
}
