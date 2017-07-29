package pl.north93.zgame.api.global.redis.observable.impl;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.observable.Hash;
import pl.north93.zgame.api.global.redis.observable.Value;

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
    public String getName()
    {
        return this.name;
    }

    @Override
    public void put(final String key, final V value)
    {
        this.observer.getRedis().hset(this.name, key, this.observer.getMsgPack().serialize(this.valueClass, value));
    }

    /*default*/ void put(final String key, final byte[] bytes)
    {
        this.observer.getRedis().hset(this.name, key, bytes);
    }

    @Override
    public V get(final String key)
    {
        return this.deserialize(this.observer.getRedis().hget(this.name, key));
    }

    @Override
    public Value<V> getAsValue(final String key)
    {
        return this.observer.get(this, key);
    }

    @Override
    public Set<String> keys()
    {
        return Sets.newHashSet(this.observer.getRedis().hkeys(this.name));
    }

    @Override
    public Set<V> values()
    {
        return this.observer.getRedis().hvals(this.name).stream().map(this::deserialize).collect(Collectors.toSet());
    }

    @Override
    public void delete(final String key)
    {
        this.observer.getRedis().hdel(this.name, key);
    }

    @Override
    public boolean exists(final String key)
    {
        return this.observer.getRedis().hexists(this.name, key);
    }

    @Override
    public long size()
    {
        return this.observer.getRedis().hlen(this.name);
    }

    @Override
    public void clear()
    {
        this.observer.getRedis().del(this.name);
    }

    private V deserialize(final byte[] bytes)
    {
        if (bytes == null)
        {
            return null; // there is nothing to deserialize. Prevent NPE from deserializer.
        }
        return this.observer.getMsgPack().deserialize(this.valueClass, bytes);
    }

    /*default*/ Class<V> getClazz()
    {
        return this.valueClass;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("valueClass", this.valueClass).append("name", this.name).toString();
    }
}
