package pl.north93.northplatform.api.global.redis.observable.impl;

import java.util.concurrent.TimeUnit;

import io.lettuce.core.api.sync.RedisCommands;
import lombok.ToString;
import pl.north93.northplatform.api.global.redis.observable.Lock;
import pl.north93.northplatform.api.global.redis.observable.ObjectKey;
import pl.north93.northplatform.api.global.storage.StorageConnector;

@ToString(of = {"clazz", "objectKey", "cache"})
class CachedValueImpl<T> extends CachedValue<T>
{
    private final Class<T> clazz;
    private final ObjectKey objectKey;
    private final Lock myLock;
    private T cache;

    public CachedValueImpl(final ObservationManagerImpl observationManager, final Class<T> clazz, final ObjectKey objectKey)
    {
        super(observationManager);
        this.clazz = clazz;
        this.objectKey = objectKey;

        this.myLock = observationManager.getLock("caval_lock:" + this.getInternalName());
        observationManager.getValueSubHandler().addListener(this);
    }

    @Override
    /*default*/ String getInternalName()
    {
        return "key:" + this.objectKey.getKey();
    }

    @Override
    /*default*/ void handleNewValue(final byte[] newValue)
    {
        if (newValue.length == 0)
        {
            this.cache = null;
        }
        else
        {
            this.cache = this.observationManager.getMsgPack().deserialize(this.clazz, newValue);
        }
    }

    @Override
    public T get()
    {
        if (this.cache != null)
        {
            return this.cache;
        }
        return this.getFromRedis();
    }

    @Override
    public T getWithoutCache()
    {
        return this.getFromRedis();
    }

    @Override
    public synchronized T getAndDelete()
    {
        final String key = this.objectKey.getKey();

        final StorageConnector storageConnector = this.observationManager.getStorageConnector();
        final byte[] getResult = storageConnector.redisAtomically(redis ->
        {
            redis.multi();
            redis.get(key);
            redis.del(key);

            return redis.exec().get(0);
        });

        if (getResult == null)
        {
            return null;
        }

        // wysylamy aktualizacje mowiaca o usunieciu wartosci.
        this.observationManager.getValueSubHandler().update(this, new byte[0]);

        return this.observationManager.getMsgPack().deserialize(this.clazz, getResult);
    }

    private synchronized T getFromRedis()
    {
        final RedisCommands<String, byte[]> redis = this.observationManager.getRedis();
        final byte[] bytes = redis.get(this.objectKey.getKey());
        if (bytes == null)
        {
            this.cache = null;
            return null;
        }
        final T fromRedis = this.observationManager.getMsgPack().deserialize(this.clazz, bytes);
        this.cache = fromRedis;
        return fromRedis;
    }

    @Override
    public void set(final T newValue)
    {
        if (newValue == null)
        {
            this.delete();
        }
        else
        {
            this.cache = newValue;
            this.upload(-1);
        }
    }

    @Override
    public void setExpire(final T newValue, final long time, final TimeUnit timeUnit)
    {
        if (newValue == null)
        {
            this.delete();
        }
        else
        {
            this.cache = newValue;
            this.upload(timeUnit.toMillis(time));
        }
    }

    private void upload(final long time)
    {
        final byte[] serialized = this.observationManager.getMsgPack().serialize(this.clazz, this.cache);

        final RedisCommands<String, byte[]> redis = this.observationManager.getRedis();
        if (time == -1)
        {
            redis.set(this.objectKey.getKey(), serialized);
        }
        else
        {
            redis.psetex(this.objectKey.getKey(), time, serialized);
        }
        this.observationManager.getValueSubHandler().update(this, serialized);
    }

    @Override
    public boolean delete()
    {
        this.cache = null;

        final RedisCommands<String, byte[]> redis = this.observationManager.getRedis();
        final boolean success = redis.del(this.objectKey.getKey()) != 0L;
        this.observationManager.getValueSubHandler().update(this, new byte[0]);
        return success;
    }

    @Override
    public boolean isAvailable()
    {
        return this.observationManager.getRedis().exists(this.objectKey.getKey()) > 0;
    }

    @Override
    public boolean isCached()
    {
        return this.cache != null;
    }

    @Override
    public boolean expire(final int seconds)
    {
        final RedisCommands<String, byte[]> redis = this.observationManager.getRedis();
        if (seconds == -1)
        {
            return redis.persist(this.objectKey.getKey());
        }
        else
        {
            return redis.expire(this.objectKey.getKey(), seconds);
        }
    }

    @Override
    public long getTimeToLive()
    {
        return this.observationManager.getRedis().ttl(this.objectKey.getKey());
    }

    @Override
    public final Lock getLock()
    {
        return this.myLock;
    }
}
