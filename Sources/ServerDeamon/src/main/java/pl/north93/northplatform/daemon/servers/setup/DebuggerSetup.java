package pl.north93.northplatform.daemon.servers.setup;

import java.text.MessageFormat;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Named;
import pl.north93.northplatform.api.global.network.impl.servers.ServerDto;
import pl.north93.northplatform.api.global.utils.JavaArguments;
import pl.north93.northplatform.daemon.cfg.DaemonConfig;
import pl.north93.northplatform.daemon.event.ServerCreatingEvent;

@Slf4j
public class DebuggerSetup
{
    private final DaemonConfig daemonConfig;

    @Bean
    private DebuggerSetup(final DaemonConfig daemonConfig, final @Named("daemon") EventBus eventBus)
    {
        this.daemonConfig = daemonConfig;
        eventBus.register(this);
    }

    @Subscribe
    public void setupDebugger(final ServerCreatingEvent event)
    {
        final String debuggerHost = this.daemonConfig.debuggerHost;
        if (StringUtils.isEmpty(debuggerHost))
        {
            return;
        }

        final JavaArguments java = event.getArguments();
        final ServerDto serverDto = event.getServerDtoValue().get();

        final int portOffset = serverDto.getConnectPort() - this.daemonConfig.portRangeStart;
        final int debuggerPort = this.daemonConfig.debuggerRangeStart + portOffset;

        java.addJavaArg(this.formatArgument(debuggerHost, debuggerPort));
        log.info("Attached debugger on host {} and port {}", debuggerHost, debuggerPort);
    }

    private String formatArgument(final String host, final int port)
    {
        return MessageFormat.format("agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address={0}:{1}", host, Integer.toString(port));
    }
}
