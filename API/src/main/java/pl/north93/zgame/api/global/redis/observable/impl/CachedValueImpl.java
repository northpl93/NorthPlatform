package pl.north93.zgame.api.global.redis.observable.impl;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.observable.Lock;
import pl.north93.zgame.api.global.redis.observable.ObjectKey;
import pl.north93.zgame.api.global.redis.observable.Value;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

class CachedValueImpl<T> implements Value<T>
{
    private final ObservationManagerImpl observationManager;
    private final Class<T>               clazz;
    private final ObjectKey              objectKey;
    private final Lock                   myLock;
    private       T                      cache;

    public CachedValueImpl(final ObservationManagerImpl observationManager, final Class<T> clazz, final ObjectKey objectKey)
    {
        this.observationManager = observationManager;
        this.clazz = clazz;
        this.objectKey = objectKey;
        this.myLock = observationManager.getLock("lock:" + objectKey.getKey());
        observationManager.getRedisSubscriber().subscribe(this.getChannelKey(), new ValueSubscriptionHandler(this));
    }

    private String getChannelKey()
    {
        return "newvalue:" + this.objectKey.getKey();
    }

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
    public T getOr(final Supplier<T> defaultValue)
    {
        if (this.isCached())
        {
            return this.cache;
        }
        else if (this.isAvailable())
        {
            return this.getFromRedis();
        }
        else
        {
            this.set(defaultValue.get());
            return this.cache;
        }
    }

    @Override
    public T getAndDelete()
    {
        final String key = this.objectKey.getKey();
        final Response<byte[]> response;

        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            final Transaction multi = jedis.multi();
            response = multi.get(key.getBytes());
            multi.del(key);
            multi.exec();
        }

        final byte[] bytes = response.get();
        if (bytes == null)
        {
            return null;
        }
        return this.observationManager.getMsgPack().deserialize(this.clazz, bytes);
    }

    @Override
    public boolean update(final Function<T, T> update)
    {
        try
        {
            this.lock();
            final T t = this.get();
            if (t != null)
            {
                this.set(update.apply(t));
                return true;
            }

            return false;
        }
        finally
        {
            this.unlock();
        }
    }

    @Override
    public void get(final Consumer<T> callback)
    {
        if (this.isCached())
        {
            callback.accept(this.cache);
        }
        else
        {
            this.observationManager.getPlatformConnector().runTaskAsynchronously(() -> callback.accept(this.getFromRedis()));
        }
    }

    @Override
    public void ifPresent(final Consumer<T> action)
    {
        final T value = this.get();
        if (value != null)
        {
            action.accept(value);
        }
    }

    private synchronized T getFromRedis()
    {
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            final byte[] bytes = jedis.get(this.objectKey.getKey().getBytes());
            if (bytes == null)
            {
                this.cache = null;
                return null;
            }
            final T fromRedis = this.observationManager.getMsgPack().deserialize(this.clazz, bytes);
            this.cache = fromRedis;
            return fromRedis;
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
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

        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            if (time == -1)
            {
                jedis.set(this.objectKey.getKey().getBytes(), serialized);
            }
            else
            {
                jedis.psetex(this.objectKey.getKey().getBytes(), time, serialized);
            }
            jedis.publish(this.getChannelKey().getBytes(), serialized);
        }
    }

    @Override
    public Value<T> setIfUnavailable(final Supplier<T> defaultValue)
    {
        if (! this.isAvailable())
        {
            this.set(defaultValue.get());
        }
        return this;
    }

    @Override
    public boolean delete()
    {
        this.cache = null;
        final boolean success;
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            success = jedis.del(this.objectKey.getKey()) != 0L;
            jedis.publish(this.getChannelKey().getBytes(), new byte[0]);
        }
        return success;
    }

    @Override
    public boolean isAvailable()
    {
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            return jedis.exists(this.objectKey.getKey());
        }
    }

    @Override
    public boolean isCached()
    {
        return this.cache != null;
    }

    @Override
    public void expire(final int seconds)
    {
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            if (seconds == -1)
            {
                jedis.persist(this.objectKey.getKey());
            }
            else
            {
                jedis.expire(this.objectKey.getKey(), seconds);
            }
        }
    }

    @Override
    public long getTimeToLive()
    {
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            return jedis.ttl(this.objectKey.getKey());
        }
    }

    @Override
    public Lock getLock()
    {
        return this.myLock;
    }

    @Override
    public void lock()
    {
        this.myLock.lock();
    }

    @Override
    public void unlock()
    {
        this.myLock.unlock();
    }

    @Override
    protected void finalize() throws Throwable
    {
        this.observationManager.getRedisSubscriber().unSubscribe(this.getChannelKey());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("clazz", this.clazz).append("objectKey", this.objectKey).append("cache", this.cache).toString();
    }
}
