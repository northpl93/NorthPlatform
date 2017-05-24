package pl.north93.zgame.api.global.redis.observable.impl;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.subscriber.SubscriptionHandler;

class ValueSubscriptionHandler implements SubscriptionHandler
{
    /*default*/ final static String CHANNEL_PREFIX = "caval_upd:";
    private final ObservationManagerImpl                     observationManager;
    private final Map<String, WeakReference<CachedValue<?>>> listeners;

    public ValueSubscriptionHandler(final ObservationManagerImpl observationManager)
    {
        this.observationManager = observationManager;
        this.listeners = new HashMap<>(128);
    }

    public void addListener(final CachedValue<?> value)
    {
        synchronized (this.listeners)
        {
            this.listeners.put(value.getInternalName(), new WeakReference<>(value));
        }
    }

    public void removeListener(final CachedValue<?> value)
    {
        synchronized (this.listeners)
        {
            this.listeners.remove(value.getInternalName());
        }
    }

    public void update(final CachedValue<?> value, final byte[] message)
    {
        final String channel = CHANNEL_PREFIX + value.getInternalName();
        this.observationManager.getRedisSubscriber().publish(channel, message);
    }

    @Override
    public void handle(final String channel, final byte[] message)
    {
        final String name = channel.substring(CHANNEL_PREFIX.length());

        final CachedValue<?> value;
        synchronized (this.listeners)
        {
            final WeakReference<CachedValue<?>> valueRef = this.listeners.get(name);
            if (valueRef == null || (value = valueRef.get()) == null)
            {
                return;
            }
        }

        value.handleNewValue(message);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
