package pl.north93.northplatform.daemon.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.network.impl.servers.ServerDto;

public class ServerDeathEvent
{
    private final ServerDto server;

    public ServerDeathEvent(final ServerDto server)
    {
        this.server = server;
    }

    public ServerDto getServer()
    {
        return this.server;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("server", this.server).toString();
    }
}
