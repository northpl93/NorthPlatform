package pl.north93.northplatform.api.global.redis.observable.impl;

import javax.annotation.Nonnull;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.lambdaworks.redis.ScriptOutputType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.redis.observable.Cache;
import pl.north93.northplatform.api.global.redis.observable.ObjectKey;
import pl.north93.northplatform.api.global.redis.observable.Value;

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
        return (int) this.observationManager.getRedis().eval("return #redis.call('keys', '" + this.prefix + "*')", ScriptOutputType.INTEGER);
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
        if (this.expire > 0)
        {
            remoteValue.setExpire(value, this.expire, TimeUnit.SECONDS);
        }
        else
        {
            remoteValue.set(value);
        }
    }

    @Override
    public Optional<V> get(final K key)
    {
        final ObjectKey objectKey = this.keyMapper.apply(key);
        final Value<V> vValue = this.observationManager.get(this.valueClass, new ObjectKey(this.prefix, objectKey));
        return Optional.ofNullable(this.provider != null ? vValue.getOr(() -> this.provider.apply(key)) : vValue.get());
    }

    @Override
    @Nonnull
    public Value<V> getValue(final K key)
    {
        final Value<V> value = this.getValue0(key);
        if (this.provider != null && ! value.isPreset())
        {
            value.set(this.provider.apply(key));
        }
        return value;
    }

    private Value<V> getValue0(final K key)
    {
        final ObjectKey objectKey = this.keyMapper.apply(key);
        return this.observationManager.get(this.valueClass, new ObjectKey(this.prefix, objectKey));
    }

    @Override
    public void clear()
    {
        this.observationManager.getRedis().eval("local k=redis.call('keys',KEYS[1])for i=1,#k,5000 do redis.call('del',unpack(k,i,math.min(i+4999,#k)))end", ScriptOutputType.INTEGER, this.prefix);
    }

    @Override
    public boolean contains(final K key)
    {
        return this.getValue0(key).isPreset();
    }

    @Override
    public void remove(final K key)
    {
        this.getValue0(key).delete();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("keyClass", this.keyClass).append("valueClass", this.valueClass).append("prefix", this.prefix).toString();
    }
}
