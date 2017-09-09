package pl.north93.zgame.api.global.network.proxy;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ProxyDto
{
    private String  id;
    private String  hostname;
    private Integer onlinePlayers;

    public ProxyDto()
    {
    }

    public String getId()
    {
        return this.id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public String getHostname()
    {
        return this.hostname;
    }

    public void setHostname(final String hostname)
    {
        this.hostname = hostname;
    }

    public Integer getOnlinePlayers()
    {
        return this.onlinePlayers;
    }

    public void setOnlinePlayers(final Integer onlinePlayers)
    {
        this.onlinePlayers = onlinePlayers;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("id", this.id).append("onlinePlayers", this.onlinePlayers).toString();
    }
}
