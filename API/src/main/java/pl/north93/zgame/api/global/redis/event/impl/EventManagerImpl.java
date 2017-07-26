package pl.north93.zgame.api.global.redis.event.impl;

import java.lang.reflect.Method;
import java.util.Collection;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.component.annotations.bean.Named;
import pl.north93.zgame.api.global.redis.event.IEventManager;
import pl.north93.zgame.api.global.redis.event.INetEvent;
import pl.north93.zgame.api.global.redis.event.NetEventSubscriber;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriber;

public class EventManagerImpl extends Component implements IEventManager
{
    @Inject
    private TemplateManager msgPack;
    @Inject
    private RedisSubscriber subscriber;
    private final Multimap<Class<?>, IEventInvocationHandler> handlers = ArrayListMultimap.create();

    @Override
    public void callEvent(final INetEvent event)
    {
        final byte[] serialized = this.msgPack.serialize(INetEvent.class, event);
        this.subscriber.publish("net_events", serialized);
    }

    private void handleReceiving(final String channel, final byte[] bytes)
    {
        final INetEvent event;
        try
        {
            event = this.msgPack.deserialize(INetEvent.class, bytes);
        }
        catch (final RuntimeException e)
        {
            // nie udalo sie zdeserializowac, nie ma takiej klasy, whatever
            return;
        }

        synchronized (this.handlers)
        {
            final Collection<IEventInvocationHandler> handlers = this.handlers.get(event.getClass());
            for (final IEventInvocationHandler handler : handlers)
            {
                handler.invoke(event);
            }
        }
    }

    @Aggregator(NetEventSubscriber.class)
    private void aggregateHandlers(final NetEventSubscriber annotation, final Method target, final @Named("MethodOwner") Object targetInstance)
    {
        synchronized (this.handlers)
        {
            this.handlers.put(annotation.value(), new MethodInvocationHandler(targetInstance, target));
        }
    }

    @Override
    protected void enableComponent()
    {
        this.subscriber.subscribe("net_events", this::handleReceiving);
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("handlers", this.handlers).toString();
    }
}
