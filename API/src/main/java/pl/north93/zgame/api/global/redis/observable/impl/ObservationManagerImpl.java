package pl.north93.zgame.api.global.redis.observable.impl;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.PlatformConnector;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.data.StorageConnector;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.ObjectKey;
import pl.north93.zgame.api.global.redis.observable.ProvidingRedisKey;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriber;
import redis.clients.jedis.JedisPool;

public class ObservationManagerImpl extends Component implements IObservationManager
{
    private final Map<String, WeakReference<Value<?>>> cache = new HashMap<>();
    @InjectComponent("API.Database.StorageConnector")
    private StorageConnector storageConnector;
    @InjectComponent("API.Database.Redis.MessagePackSerializer")
    private TemplateManager  msgPack;
    @InjectComponent("API.Database.Redis.Subscriber")
    private RedisSubscriber  redisSubscriber;

    @Override
    public <T> Value<T> get(final Class<T> clazz, final String objectKey)
    {
        return this.get(clazz, new ObjectKey(objectKey));
    }

    @Override
    public <T> Value<T> get(final Class<T> clazz, final ObjectKey objectKey)
    {
        final String key = objectKey.getKey();

        WeakReference<Value<?>> value = this.cache.get(key);
        if (value == null || value.get() == null)
        {
            value = new WeakReference<>(new ValueImpl<>(this, clazz, objectKey));
            this.cache.put(key, value);
        }

        //noinspection unchecked
        return (Value<T>) value.get();
    }

    @Override
    public <T> Value<T> get(final Class<T> clazz, final ProvidingRedisKey keyProvider)
    {
        return this.get(clazz, keyProvider.getKey());
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
    protected void enableComponent()
    {
    }

    @Override
    protected void disableComponent()
    {
        this.cache.clear();
    }

    /*default*/ PlatformConnector getPlatformConnector()
    {
        return this.getApiCore().getPlatformConnector();
    }

    /*default*/ JedisPool getJedis()
    {
        return this.storageConnector.getJedisPool();
    }

    /*default*/ TemplateManager getMsgPack()
    {
        return this.msgPack;
    }

    /*default*/ RedisSubscriber getRedisSubscriber()
    {
        return this.redisSubscriber;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("cache", this.cache).toString();
    }
}
