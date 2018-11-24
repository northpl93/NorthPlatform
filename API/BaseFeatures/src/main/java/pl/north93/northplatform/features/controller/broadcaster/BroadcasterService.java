package pl.north93.northplatform.features.controller.broadcaster;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.config.ConfigUpdatedNetEvent;
import pl.north93.northplatform.api.global.config.IConfig;
import pl.north93.northplatform.api.global.config.NetConfig;
import pl.north93.northplatform.api.global.network.event.NetworkShutdownNetEvent;
import pl.north93.northplatform.api.global.redis.event.NetEventSubscriber;
import pl.north93.northplatform.features.controller.broadcaster.cfg.BroadcasterCfg;
import pl.north93.northplatform.features.controller.broadcaster.cfg.BroadcasterEntryCfg;

public class BroadcasterService
{
    private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1); // todo zamienic na ogólny scheduler platformy
    private final List<ScheduledFuture<?>> futures         = new ArrayList<>();
    @Inject @NetConfig(id="broadcaster", type = BroadcasterCfg.class)
    private IConfig<BroadcasterCfg> config;

    @Bean
    private BroadcasterService()
    {
        final BroadcasterCfg config = this.config.get();
        if (config != null)
        {
            this.updateEntries(config);
        }
    }

    @NetEventSubscriber(ConfigUpdatedNetEvent.class)
    private void updateBroadcasters(final ConfigUpdatedNetEvent event)
    {
        if (! event.getConfigName().equals("broadcaster"))
        {
            return;
        }

        this.updateEntries(this.config.get());
    }

    @NetEventSubscriber(NetworkShutdownNetEvent.class)
    private void cleanUpBeforeShutdown(final NetworkShutdownNetEvent event)
    {
        this.stopAllBroadcasters();
        this.executorService.shutdown();
    }

    private void updateEntries(final BroadcasterCfg broadcasterCfg)
    {
        this.stopAllBroadcasters();

        for (final BroadcasterEntryCfg entryCfg : broadcasterCfg.getEntries())
        {
            final BroadcasterEntry entry = new BroadcasterEntry(entryCfg);
            final int interval = entryCfg.getInterval();

            final ScheduledFuture<?> future = this.executorService.scheduleAtFixedRate(entry, interval, interval, TimeUnit.SECONDS);
            this.futures.add(future);
        }
    }

    private void stopAllBroadcasters()
    {
        for (final ScheduledFuture<?> future : this.futures)
        {
            future.cancel(false);
        }

        this.futures.clear();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("futures", this.futures).append("config", this.config).toString();
    }
}
