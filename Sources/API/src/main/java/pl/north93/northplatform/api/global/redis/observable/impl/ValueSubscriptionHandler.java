package pl.north93.northplatform.api.global.redis.observable.impl;

import java.util.Map;

import lombok.ToString;
import pl.north93.northplatform.api.global.finalizer.FinalizerUtils;
import pl.north93.northplatform.api.global.redis.subscriber.SubscriptionHandler;
import pl.north93.northplatform.api.global.utils.ReferenceHashMap;

@ToString(of = "listeners")
class ValueSubscriptionHandler implements SubscriptionHandler
{
    /*default*/ final static String CHANNEL_PREFIX = "caval_upd:";
    private final ObservationManagerImpl      observationManager;
    private final Map<String, CachedValue<?>> listeners;

    public ValueSubscriptionHandler(final ObservationManagerImpl observationManager)
    {
        this.observationManager = observationManager;
        this.listeners = new ReferenceHashMap<>(ReferenceHashMap.ReferenceType.WEAK);
    }

    public void addListener(final CachedValue<?> value)
    {
        final String internalName = value.getInternalName();
        this.listeners.put(internalName, value); // mapa jest wewnętrznie lockowana

        FinalizerUtils.register(value, () -> this.removeListener(internalName));
    }

    private void removeListener(final String internalName)
    {
        this.listeners.remove(internalName); // mapa jest wewnętrznie lockowana
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

        final CachedValue<?> cachedValue = this.listeners.get(name); // mapa jest wewnętrznie lockowana
        if (cachedValue == null)
        {
            return;
        }

        cachedValue.handleNewValue(message);
    }
}
