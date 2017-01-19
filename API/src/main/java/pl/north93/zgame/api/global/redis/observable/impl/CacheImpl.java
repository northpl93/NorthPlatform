package pl.north93.zgame.api.global.redis.observable.impl;

import java.util.function.Function;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.observable.Cache;
import pl.north93.zgame.api.global.redis.observable.ObjectKey;
import pl.north93.zgame.api.global.redis.observable.Value;
import redis.clients.jedis.Jedis;

class CacheImpl<K, V> implements Cache<K, V>
{
    private final ObservationManagerImpl observationManager;
    private final Class<K>               keyClass;
    private final Class<V>               valueClass;
    private final Function<K, ObjectKey> keyMapper;
    private final Function<K, V>         provider;
    private final String                 prefix;
    private final int                    expire;

    public CacheImpl(final ObservationManagerImpl observationManager, final Class<K> keyClass, final Class<V> valueClass, final Function<K, ObjectKey> keyMapper, final Function<K, V> provider, final String prefix, final int expire)
    {
        this.observationManager = observationManager;
        this.keyClass = keyClass;
        this.valueClass = valueClass;
        this.keyMapper = keyMapper;
        this.provider = provider;
        this.prefix = prefix;
        this.expire = expire;
    }

    @Override
    public int size()
    {
        try (final Jedis resource = this.observationManager.getJedis().getResource())
        {
            return (int) resource.eval("return #redis.call('keys', '" + this.prefix + "*')", 0);
        }
    }

    @Override
    public boolean isEmpty()
    {
        return this.size() == 0;
    }

    @Override
    public void put(final K key, final V value)
    {
        final Value<V> remoteValue = this.getValue(key);
        remoteValue.set(value);
        if (this.expire > 0)
        {
            remoteValue.expire(this.expire);
        }
    }

    @Override
    public V get(final K key)
    {
        final ObjectKey objectKey = this.keyMapper.apply(key);
        final Value<V> vValue = this.observationManager.get(this.valueClass, new ObjectKey(this.prefix, objectKey));
        return this.provider != null ? vValue.getOr(() -> this.provider.apply(key)) : vValue.get();
    }

    @Override
    public Value<V> getValue(final K key)
    {
        final ObjectKey objectKey = this.keyMapper.apply(key);
        final Value<V> vValue = this.observationManager.get(this.valueClass, new ObjectKey(this.prefix, objectKey));
        if (this.provider != null && ! vValue.isAvailable())
        {
            vValue.set(this.provider.apply(key));
        }
        return vValue;
    }

    @Override
    public void clear()
    {
        try (final Jedis resource = this.observationManager.getJedis().getResource())
        {
            resource.eval("local k=redis.call('keys',ARGV[1])for i=1,#k,5000 do redis.call('del',unpack(k,i,math.min(i+4999,#k)))end", 0, this.prefix);
        }
    }

    @Override
    public boolean contains(final K key)
    {
        return this.getValue(key).isAvailable();
    }

    @Override
    public void remove(final K key)
    {
        this.getValue(key).delete();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("keyClass", this.keyClass).append("valueClass", this.valueClass).append("prefix", this.prefix).toString();
    }
}
