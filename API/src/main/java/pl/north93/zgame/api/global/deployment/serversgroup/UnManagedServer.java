package pl.north93.zgame.api.global.deployment.serversgroup;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComment;

public class UnManagedServer
{
    @CfgComment("Identyfikator serwera. Należy go podać podczas uruchamiania serwera.")
    private String  serverId;
    @CfgComment("Adres do łączenia się z tym serwerem.")
    private String  connectIp;
    @CfgComment("Port do łączenia się z tym serwerem.")
    private Integer connectPort;

    public UnManagedServer()
    {
    }

    public UnManagedServer(final String serverId, final String connectIp, final Integer connectPort)
    {
        this.serverId = serverId;
        this.connectIp = connectIp;
        this.connectPort = connectPort;
    }

    public String getServerId()
    {
        return this.serverId;
    }

    public void setServerId(final String serverId)
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
