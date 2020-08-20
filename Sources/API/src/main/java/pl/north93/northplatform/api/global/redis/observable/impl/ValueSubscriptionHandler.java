package pl.north93.northplatform.api.global.redis.observable.impl;

import java.util.Map;

import com.google.common.collect.MapMaker;

import lombok.ToString;
import pl.north93.northplatform.api.global.redis.subscriber.RedisSubscriber;
import pl.north93.northplatform.api.global.redis.subscriber.SubscriptionHandler;

@ToString(of = "listeners")
class ValueSubscriptionHandler implements SubscriptionHandler
{
    /*default*/ final static String CHANNEL_PREFIX = "caval_upd:";
    private final RedisSubscriber redisSubscriber;
    private final Map<String, CachedValue<?>> listeners;

    public ValueSubscriptionHandler(final RedisSubscriber redisSubscriber)
    {
        this.redisSubscriber = redisSubscriber;
        this.listeners = new MapMaker().weakValues().makeMap();
    }

    public void registerListener(final CachedValue<?> value)
    {
        final String internalName = value.getInternalName();
        this.listeners.put(internalName, value); // mapa jest wewnętrznie lockowana
    }

    public void broadcastUpdate(final CachedValue<?> value, final byte[] message)
    {
        final String channel = CHANNEL_PREFIX + value.getInternalName();
        this.redisSubscriber.publish(channel, message);
    }

    @Override
    public void handle(final String channel, final byte[] message)
    {
        final String name = channel.substring(CHANNEL_PREFIX.length());

        final CachedValue<?> cachedValue = this.listeners.get(name); // mapa jest wewnętrznie lockowana
        if (cachedValue == null)
        {
            return;
        }

        cachedValue.handleNewValue(message);
    }
}
