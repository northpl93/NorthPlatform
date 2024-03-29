package pl.north93.northplatform.api.global.redis.event.impl;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.IComponentManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Aggregator;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.component.annotations.bean.Named;
import pl.north93.northplatform.api.global.redis.event.IEventManager;
import pl.north93.northplatform.api.global.redis.event.INetEvent;
import pl.north93.northplatform.api.global.redis.event.NetEventSubscriber;
import pl.north93.northplatform.api.global.redis.subscriber.RedisSubscriber;
import pl.north93.serializer.platform.NorthSerializer;

@Slf4j
public class EventManagerImpl extends Component implements IEventManager
{
    @Inject
    private RedisSubscriber subscriber;
    @Inject
    private IComponentManager componentManager;
    @Inject
    private NorthSerializer<byte[], byte[]> msgPack;
    private final Set<String> subscribedClasses = new HashSet<>();
    private final Multimap<Class<?>, IEventInvocationHandler> handlers = ArrayListMultimap.create();

    @Override
    public void callEvent(final INetEvent event)
    {
        final String className = event.getClass().getName();
        final String channelName = "net_events:" + className;

        final byte[] serialized = this.msgPack.serialize(event);
        this.subscriber.publish(channelName, serialized);
    }

    private void registerListener(final IEventInvocationHandler handler, final Class<?>... classes)
    {
        for (final Class<?> eventClass : classes)
        {
            final String name = eventClass.getName();
            if (this.subscribedClasses.add(name))
            {
                // dodajemy pierwszy listener tego typu klasy wiec musimy zasubskrybowac kanal
                // w redisie
                final String channelName = this.getChannelNameFromClass(eventClass);
                this.subscriber.subscribe(channelName, this::handleReceiving);
            }

            // standardowo dodajemy do mapy handlerów nasz nowy handler
            this.handlers.put(eventClass, handler);
        }
    }

    @Aggregator(NetEventSubscriber.class)
    private void aggregateHandlers(final NetEventSubscriber annotation, final Method target, final @Named("MethodOwner") Object targetInstance)
    {
        synchronized (this.handlers)
        {
            this.registerListener(new MethodInvocationHandler(targetInstance, target), annotation.value());
        }
    }

    @SuppressWarnings("unchecked")
    private void handleReceiving(final String channel, final byte[] bytes)
    {
        final String className = this.getClassNameFromChannel(channel);

        final INetEvent event;
        try
        {
            final Class<? extends INetEvent> clazz = (Class) this.componentManager.findClass(className);
            event = this.msgPack.deserialize(clazz, bytes);
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
                this.callInvocationHandler(handler, event);
            }
        }
    }

    private void callInvocationHandler(final IEventInvocationHandler eventInvocationHandler, final INetEvent event)
    {
        try
        {
            eventInvocationHandler.invoke(event);
        }
        catch (final Exception e)
        {
            final String eventName = event.getClass().getName();
            log.error("Exception thrown while executing event handler of {}", eventName, e);
        }
    }

    // pobieramy nazwe klasy z nazwy kanalu, aby przyspieszyc deserializacje
    private String getClassNameFromChannel(final String channelName)
    {
        final int prefixLength = 11;
        return channelName.substring(prefixLength);
    }

    // generuje nazwe kanalu przez który sa przesylane eventy danego typu
    private String getChannelNameFromClass(final Class<?> clazz)
    {
        return "net_events:" + clazz.getName();
    }

    @Override
    protected void enableComponent()
    {
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
