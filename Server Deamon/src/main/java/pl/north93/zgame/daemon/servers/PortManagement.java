package pl.north93.zgame.daemon.servers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.impl.ServerDto;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.utils.JavaArguments;
import pl.north93.zgame.daemon.cfg.DaemonConfig;

public class PortManagement
{
    @Inject
    private DaemonConfig config;
    private final Set<Integer> freePorts = new HashSet<>();
    private int firstFree;

    @Bean
    private PortManagement()
    {
        this.firstFree = this.config.portRangeStart;
    }

    public synchronized int getFreePort()
    {
        final Iterator<Integer> iterator = this.freePorts.iterator();
        if (iterator.hasNext())
        {
            final Integer port = iterator.next();
            iterator.remove();
            return port;
        }

        return this.firstFree++;
    }

    public synchronized void returnPort(final int port)
    {
        this.freePorts.add(port);
    }

    public void setupNetwork(final Value<ServerDto> serverDtoValue, final JavaArguments arguments)
    {
        final String listenHost = this.config.listenHost;
        final String externalHost = this.config.externalHost;
        final int port = this.getFreePort();

        arguments.addProgramVar("--host " + listenHost);
        arguments.addProgramVar("--port " + port);

        serverDtoValue.update(serverDto ->
        {
            serverDto.setConnectIp(externalHost);
            serverDto.setConnectPort(port);
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("freePorts", this.freePorts).append("firstFree", this.firstFree).toString();
    }
}
