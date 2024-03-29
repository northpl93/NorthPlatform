package pl.north93.northplatform.daemon.network;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.component.annotations.bean.Named;
import pl.north93.northplatform.api.global.network.daemon.DaemonDto;
import pl.north93.northplatform.api.global.network.daemon.IDaemonsManager;
import pl.north93.northplatform.api.global.network.event.NetworkShutdownNetEvent;
import pl.north93.northplatform.api.global.redis.event.NetEventSubscriber;
import pl.north93.northplatform.api.global.redis.observable.Hash;
import pl.north93.northplatform.api.global.redis.observable.Value;
import pl.north93.northplatform.daemon.cfg.DaemonConfig;
import pl.north93.northplatform.daemon.event.ServerCreatingEvent;
import pl.north93.northplatform.daemon.event.ServerExitedEvent;

@Slf4j
public class DaemonInfoHandler
{
    @Inject
    private DaemonConfig config;
    @Inject
    private ApiCore apiCore;
    @Inject
    private IDaemonsManager daemonsManager;
    private final Value<DaemonDto> daemonInfo;

    @Bean
    private DaemonInfoHandler(final @Named("daemon") EventBus eventBus)
    {
        final String id = this.apiCore.getId();
        final DaemonDto daemon = new DaemonDto(id, this.apiCore.getHostName(), this.config.maxMemory, 0, 0, true);

        final Hash<DaemonDto> daemons = this.daemonsManager.unsafe().getHash();
        daemons.put(id, daemon);
        this.daemonInfo = daemons.getAsValue(id);

        eventBus.register(this);
    }

    @NetEventSubscriber(NetworkShutdownNetEvent.class)
    public void handleNetworkShutdownEvent(final NetworkShutdownNetEvent event)
    {
        // blokujemy akceptowanie nowych serwerów gdy zostala rozpoczeta procedura
        // wylaczania sieci
        this.setAcceptingNewServers(false);
    }

    public DaemonDto getDaemonInfo()
    {
        return this.daemonInfo.get();
    }

    public void delete()
    {
        this.daemonInfo.delete();
        log.info("Daemon info deleted from redis");
    }

    public void setAcceptingNewServers(final boolean acceptingNewServers)
    {
        this.daemonInfo.update(daemon ->
        {
            log.info("Switched accepting new servers: {}", acceptingNewServers);
            return new DaemonDto(daemon.getName(), daemon.getHostName(), daemon.getMaxRam(), daemon.getRamUsed(), daemon.getServerCount(), acceptingNewServers);
        });
    }

    @Subscribe
    public void onServerCreating(final ServerCreatingEvent event)
    {
        this.updateStats(event.getPattern().getMaxMemory(), 1);
    }

    @Subscribe
    public void onServerExited(final ServerExitedEvent event)
    {
        this.updateStats(- event.getInstance().getPattern().getMaxMemory(), - 1);
    }

    private void updateStats(final int ram, final int servers)
    {
        this.daemonInfo.update(daemon ->
        {
            final int ramUsed = daemon.getRamUsed() + ram;
            final int serverCount = daemon.getServerCount() + servers;
            return new DaemonDto(daemon.getName(), daemon.getHostName(), daemon.getMaxRam(), ramUsed, serverCount, daemon.isAcceptingServers());
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("daemonInfo", this.daemonInfo).toString();
    }
}
