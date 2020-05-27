package pl.north93.northplatform.api.global.redis.observable.impl;

import java.util.Map;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.lettuce.core.api.sync.RedisCommands;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.redis.observable.Hash;
import pl.north93.northplatform.api.global.redis.observable.ICacheBuilder;
import pl.north93.northplatform.api.global.redis.observable.IObservationManager;
import pl.north93.northplatform.api.global.redis.observable.Lock;
import pl.north93.northplatform.api.global.redis.observable.ObjectKey;
import pl.north93.northplatform.api.global.redis.observable.SortedSet;
import pl.north93.northplatform.api.global.redis.observable.Value;
import pl.north93.northplatform.api.global.redis.subscriber.RedisSubscriber;
import pl.north93.northplatform.api.global.storage.StorageConnector;
import pl.north93.serializer.platform.NorthSerializer;

public class ObservationManagerImpl extends Component implements IObservationManager
{
    private final Map<String, CachedValue<?>> cachedValues;
    private final ValueSubscriptionHandler valueSubHandler;
    private LockManagement lockManagement;
    @Inject
    private StorageConnector storageConnector;
    @Inject
    private RedisSubscriber redisSubscriber;
    @Inject
    private NorthSerializer<byte[], byte[]> msgPack;

    public ObservationManagerImpl()
    {
        this.cachedValues = this.instantiateCache().asMap();
        this.valueSubHandler = new ValueSubscriptionHandler(this);
    }

    private Cache<String, CachedValue<?>> instantiateCache()
    {
        return CacheBuilder.newBuilder().recordStats().softValues().build();
    }

    @Override
    public <T> Value<T> get(final Class<T> clazz, final String objectKey)
    {
        return this.get(clazz, new ObjectKey(objectKey));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Value<T> get(final Class<T> clazz, final ObjectKey objectKey)
    {
        final String key = objectKey.getKey();

        return (Value<T>) this.cachedValues.computeIfAbsent(key, p1 -> new CachedValueImpl<>(this, clazz, objectKey));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Value<T> get(final Hash<T> hash, final String key)
    {
        final HashImpl<T> hashImpl = (HashImpl<T>) hash;
        final String mapKey = "hash:" + hash.getName() + ":" + key;

        return (Value<T>) this.cachedValues.computeIfAbsent(mapKey, p1 -> new CachedHashValueImpl<>(this, hashImpl, key, hashImpl.getValueClass()));
    }

    @Override
    public <K, V> ICacheBuilder<K, V> cacheBuilder(final Class<K> keyClass, final Class<V> valueClass)
    {
        return new CacheBuilderImpl<>(this, keyClass, valueClass);
    }

    @Override
    public Lock getLock(final String name)
    {
        return new LockImpl(this.lockManagement, name);
    }

    @Override
    public Lock getMultiLock(final String... names)
    {
        final int namesLength = names.length;
        if (namesLength == 0)
        {
            throw new IllegalArgumentException("names must be not empty");
        }
        else if (namesLength == 1)
        {
            return this.getLock(names[0]);
        }

        final Lock[] locks = new Lock[namesLength];
        for (int i = 0; i < namesLength; i++)
        {
            locks[i] = this.getLock(names[i]);
        }

        return new MultiLockImpl(locks);
    }

    @Override
    public Lock getMultiLock(final Lock... locks)
    {
        return new MultiLockImpl(locks);
    }

    @Override
    public <K> SortedSet<K> getSortedSet(final String name)
    {
        return new SortedSetImpl<>(this, name);
    }

    @Override
    public <V> Hash<V> getHash(final Class<V> valueClass, final String name)
    {
        return new HashImpl<>(this, valueClass, name);
    }

    @Override
    protected void enableComponent()
    {
        this.lockManagement = new LockManagement(this.msgPack, this.storageConnector.getRedis());

        this.redisSubscriber.subscribe("unlock", this.lockManagement::unlockNotify);
        this.redisSubscriber.subscribe("__keyevent@0__:expired", this.lockManagement::unlockNotify); // nasluchujemy na przeterminowanie klucza
        this.redisSubscriber.subscribe(ValueSubscriptionHandler.CHANNEL_PREFIX + "*", this.valueSubHandler, true);
    }

    @Override
    protected void disableComponent()
    {
        synchronized (this.cachedValues)
        {
            this.cachedValues.clear();
        }
    }

    /*default*/ RedisCommands<String, byte[]> getRedis()
    {
        return this.storageConnector.getRedis();
    }

    /*default*/ StorageConnector getStorageConnector()
    {
        return this.storageConnector;
    }

    /*default*/ NorthSerializer<byte[], byte[]> getMsgPack()
    {
        return this.msgPack;
    }

    /*default*/ RedisSubscriber getRedisSubscriber()
    {
        return this.redisSubscriber;
    }

    /*default*/ ValueSubscriptionHandler getValueSubHandler()
    {
        return this.valueSubHandler;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("cachedValues", this.cachedValues).toString();
    }
}
