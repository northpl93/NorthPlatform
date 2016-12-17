package pl.north93.zgame.api.global.redis.observable.impl;

import java.util.function.Consumer;

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
        observationManager.getRedisSubscriber().subscribe(this.getChannelKey(), this::handleNewValue);
    }

    private String getChannelKey()
    {
        return "newvalue:" + this.objectKey.getKey();
    }

    private void handleNewValue(final String channel, final byte[] newValue)
    {
        this.cache = this.observationManager.getMsgPack().deserialize(this.clazz, newValue);
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

    private T getFromRedis()
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
        this.cache = newValue;
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            final byte[] serialized = this.observationManager.getMsgPack().serialize(this.clazz, newValue);

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
