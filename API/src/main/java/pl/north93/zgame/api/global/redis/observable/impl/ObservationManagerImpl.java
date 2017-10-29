package pl.north93.zgame.api.global.redis.observable.impl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.lambdaworks.redis.api.sync.RedisCommands;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.PlatformConnector;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.storage.StorageConnector;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.observable.Hash;
import pl.north93.zgame.api.global.redis.observable.ICacheBuilder;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Lock;
import pl.north93.zgame.api.global.redis.observable.ObjectKey;
import pl.north93.zgame.api.global.redis.observable.ProvidingRedisKey;
import pl.north93.zgame.api.global.redis.observable.SortedSet;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriber;
import pl.north93.zgame.api.global.utils.SoftValueHashMap;

public class ObservationManagerImpl extends Component implements IObservationManager
{
    private final Map<String, CachedValue> cachedValues = new SoftValueHashMap<>();
    private final ValueSubscriptionHandler valueSubHandler;
    private final List<LockImpl>           waitingLocks = new ArrayList<>();
    @Inject
    private StorageConnector storageConnector;
    @Inject
    private TemplateManager  msgPack;
    @Inject
    private RedisSubscriber  redisSubscriber;

    public ObservationManagerImpl()
    {
        this.valueSubHandler = new ValueSubscriptionHandler(this);
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

        return this.cachedValues.computeIfAbsent(key, p1 -> new CachedValueImpl<>(this, clazz, objectKey));
    }

    @Override
    public <T> Value<T> get(final Class<T> clazz, final ProvidingRedisKey keyProvider)
    {
        return this.get(clazz, keyProvider.getKey());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Value<T> get(final Hash<T> hash, final String key)
    {
        final HashImpl<T> hashImpl = (HashImpl<T>) hash;
        final String mapKey = "hash:" + hash.getName() + ":" + key;

        return this.cachedValues.computeIfAbsent(mapKey, p1 -> new CachedHashValueImpl<>(this, hashImpl, key, hashImpl.getClazz()));
    }

    @Override
    public <T extends ProvidingRedisKey> Value<T> of(final T preCachedObject)
    {
        //noinspection unchecked
        final Value<T> value = this.get((Class<T>) preCachedObject.getClass(), preCachedObject);
        value.set(preCachedObject);
        return value;
    }

    @Override
    public <K, V> ICacheBuilder<K, V> cacheBuilder(final Class<K> keyClass, final Class<V> valueClass)
    {
        return new CacheBuilderImpl<>(this, keyClass, valueClass);
    }

    @Override
    public Lock getLock(final String name)
    {
        return new LockImpl(this, name);
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

    /*default*/ void addWaitingLock(final LockImpl lock)
    {
        synchronized (this.waitingLocks)
        {
            this.waitingLocks.add(lock);
        }
    }

    private void unlockNotify(final String channel, final byte[] message)
    {
        final String lock = new String(message, StandardCharsets.UTF_8);
        synchronized (this.waitingLocks)
        {
            final Iterator<LockImpl> lockIter = this.waitingLocks.iterator();
            while (lockIter.hasNext())
            {
                final LockImpl waitingLock = lockIter.next();
                if (waitingLock.getName().equals(lock))
                {
                    lockIter.remove();
                    waitingLock.remoteUnlock();
                }
            }
        }
    }

    @Override
    protected void enableComponent()
    {
        this.redisSubscriber.subscribe("unlock", this::unlockNotify);
        this.redisSubscriber.subscribe("__keyevent@0__:expired", this::unlockNotify); // nasluchujemy na przeterminowanie klucza
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

    /*default*/ PlatformConnector getPlatformConnector()
    {
        return this.getApiCore().getPlatformConnector();
    }

    /*default*/ RedisCommands<String, byte[]> getRedis()
    {
        return this.storageConnector.getRedis();
    }

    /*default*/ StorageConnector getStorageConnector()
    {
        return this.storageConnector;
    }

    /*default*/ TemplateManager getMsgPack()
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

    /*default*/ Logger getMyLogger()
    {
        return this.getLogger();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("cachedValues", this.cachedValues).toString();
    }
}
