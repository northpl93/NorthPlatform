package pl.north93.northplatform.daemon.servers;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.component.annotations.bean.Named;
import pl.north93.northplatform.api.global.network.event.ServerDeathNetEvent;
import pl.north93.northplatform.api.global.network.impl.servers.ServerDto;
import pl.north93.northplatform.api.global.redis.event.IEventManager;
import pl.north93.northplatform.daemon.event.ServerDeathEvent;

@Slf4j
public class ServerDeathHandler
{
    @Inject
    private IEventManager eventManager;

    @Bean
    private ServerDeathHandler(final @Named("daemon") EventBus eventBus)
    {
        eventBus.register(this);
    }

    @Subscribe
    public void onServerDeath(final ServerDeathEvent event)
    {
        final ServerDto server = event.getServer();
        this.eventManager.callEvent(new ServerDeathNetEvent(server));

        log.warn("Server with ID {} crashed! (process exited in other state than STOPPING)", server.getUuid());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
