package pl.north93.zgame.api.global.messages;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.network.ProxyRpc;
import pl.north93.zgame.api.global.redis.rpc.Targets;

public class ProxyInstanceInfo
{
    private String  id;
    private String  hostname;
    private Integer onlinePlayers;

    public ProxyInstanceInfo()
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

    public ProxyRpc getRpc()
    {
        return API.getRpcManager().createRpcProxy(ProxyRpc.class, Targets.proxy(this.id));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("id", this.id).append("onlinePlayers", this.onlinePlayers).toString();
    }
}
