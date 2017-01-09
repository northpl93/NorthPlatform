package pl.north93.zgame.api.global.redis.observable.impl;

import java.lang.ref.WeakReference;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.subscriber.SubscriptionHandler;

class ValueSubscriptionHandler implements SubscriptionHandler
{
    private final WeakReference<CachedValueImpl<?>> wrappedValue;

    public ValueSubscriptionHandler(final CachedValueImpl<?> value)
    {
        this.wrappedValue = new WeakReference<>(value);
    }

    @Override
    public void handle(final String channel, final byte[] message)
    {
        final CachedValueImpl<?> value;
        if ((value = this.wrappedValue.get()) != null)
        {
            value.handleNewValue(message);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
