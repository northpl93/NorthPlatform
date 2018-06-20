package pl.north93.zgame.daemon.servers;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.component.annotations.bean.Named;
import pl.north93.zgame.api.global.network.event.ServerDeathNetEvent;
import pl.north93.zgame.api.global.network.impl.ServerDto;
import pl.north93.zgame.api.global.redis.event.IEventManager;
import pl.north93.zgame.daemon.event.ServerDeathEvent;

public class ServerDeathHandler
{
    @Inject
    private Logger        logger;
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

        this.logger.log(Level.WARNING, "Server with ID {0} crashed! (process exited in other state than STOPPING)", server.getUuid());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
