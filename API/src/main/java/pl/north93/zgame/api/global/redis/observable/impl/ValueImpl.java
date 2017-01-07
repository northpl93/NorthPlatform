package pl.north93.zgame.api.global.redis.observable.impl;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.observable.ObjectKey;
import pl.north93.zgame.api.global.redis.observable.Value;
import redis.clients.jedis.Jedis;

class ValueImpl<T> implements Value<T>
{
    private final ObservationManagerImpl observationManager;
    private final Class<T>               clazz;
    private final ObjectKey              objectKey;
    private T cache;

    public ValueImpl(final ObservationManagerImpl observationManager, final Class<T> clazz, final ObjectKey objectKey)
    {
        this.observationManager = observationManager;
        this.clazz = clazz;
        this.objectKey = objectKey;
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

    private synchronized T getFromRedis()
    {
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            final T fromRedis = this.observationManager.getMsgPack().deserialize(this.clazz, jedis.get(this.objectKey.getKey().getBytes()));
            this.cache = fromRedis;
            return fromRedis;
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void set(final T newValue)
    {
        if (newValue == null)
        {
            this.delete();
        }
        else
        {
            this.cache = newValue;
            this.upload();
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
    public void upload()
    {
        final byte[] serialized = this.observationManager.getMsgPack().serialize(this.clazz, this.cache);

        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            jedis.set(this.objectKey.getKey().getBytes(), serialized);
            jedis.publish(this.getChannelKey().getBytes(), serialized);
        }
    }

    @Override
    public void delete()
    {
        this.cache = null;
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            jedis.del(this.objectKey.getKey());
            jedis.publish(this.getChannelKey().getBytes(), new byte[0]);
        }
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
            jedis.expire(this.objectKey.getKey(), seconds);
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