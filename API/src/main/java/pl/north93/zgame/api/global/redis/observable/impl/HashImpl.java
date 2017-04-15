package pl.north93.zgame.api.global.redis.observable.impl;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.lambdaworks.redis.api.sync.RedisCommands;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.observable.Hash;

class HashImpl<V> implements Hash<V>
{
    private final ObservationManagerImpl observer;
    private final Class<V>               valueClass;
    private final String                 name;

    public HashImpl(final ObservationManagerImpl observer, final Class<V> valueClass, final String name)
    {
        this.observer = observer;
        this.valueClass = valueClass;
        this.name = name;
    }

    @Override
    public void put(final String key, final V value)
    {
        try (final RedisCommands<String, byte[]> redis = this.observer.getJedis())
        {
            redis.hset(this.name, key, this.observer.getMsgPack().serialize(this.valueClass, value));
        }
    }

    @Override
    public V get(final String key)
    {
        try (final RedisCommands<String, byte[]> redis = this.observer.getJedis())
        {
            return this.deserialize(redis.hget(this.name, key));
        }
    }

    @Override
    public Set<String> keys()
    {
        try (final RedisCommands<String, byte[]> redis = this.observer.getJedis())
        {
            return Sets.newHashSet(redis.hkeys(this.name));
        }
    }

    @Override
    public Set<V> values()
    {
        try (final RedisCommands<String, byte[]> redis = this.observer.getJedis())
        {
            return redis.hvals(this.name).stream().map(this::deserialize).collect(Collectors.toSet());
        }
    }

    @Override
    public void delete(final String key)
    {
        try (final RedisCommands<String, byte[]> redis = this.observer.getJedis())
        {
            redis.hdel(this.name, key);
        }
    }

    @Override
    public boolean exists(final String key)
    {
        try (final RedisCommands<String, byte[]> redis = this.observer.getJedis())
        {
            return redis.hexists(this.name, key);
        }
    }

    @Override
    public long size()
    {
        try (final RedisCommands<String, byte[]> redis = this.observer.getJedis())
        {
            return redis.hlen(this.name);
        }
    }

    @Override
    public void clear()
    {
        try (final RedisCommands<String, byte[]> redis = this.observer.getJedis())
        {
            redis.del(this.name);
        }
    }

    private V deserialize(final byte[] bytes)
    {
        return this.observer.getMsgPack().deserialize(this.valueClass, bytes);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("valueClass", this.valueClass).append("name", this.name).toString();
    }
}
