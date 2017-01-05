package pl.north93.zgame.api.global.redis.observable.impl;

import java.util.function.Function;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.observable.Cache;
import pl.north93.zgame.api.global.redis.observable.ICacheBuilder;
import pl.north93.zgame.api.global.redis.observable.ObjectKey;

class CacheBuilderImpl<K, V> implements ICacheBuilder<K, V>
{
    private final ObservationManagerImpl observationManager;
    private final Class<K>               keyClass;
    private final Class<V>               valueClass;
    private String                 name;
    private Function<K, ObjectKey> keyMapper;
    private Function<K, V>         provider;

    public CacheBuilderImpl(final ObservationManagerImpl observationManager, final Class<K> keyClass, final Class<V> valueClass)
    {
        this.observationManager = observationManager;
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    @Override
    public ICacheBuilder<K, V> name(final String name)
    {
        this.name = name;
        return this;
    }

    @Override
    public ICacheBuilder<K, V> keyMapper(final Function<K, ObjectKey> keyMapper)
    {
        this.keyMapper = keyMapper;
        return this;
    }

    @Override
    public ICacheBuilder<K, V> provider(final Function<K, V> provider)
    {
        this.provider = provider;
        return this;
    }

    @Override
    public Cache build()
    {
        if (StringUtils.isEmpty(this.name))
        {
            this.name = RandomStringUtils.random(6);
        }
        return new CacheImpl<>(this.observationManager, this.keyClass, this.valueClass, this.keyMapper, this.provider, this.name);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("observationManager", this.observationManager).append("keyMapper", this.keyMapper).append("provider", this.provider).toString();
    }
}
