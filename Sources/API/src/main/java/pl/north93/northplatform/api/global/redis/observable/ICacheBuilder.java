package pl.north93.northplatform.api.global.redis.observable;

import java.util.function.Function;

public interface ICacheBuilder<K, V>
{
    ICacheBuilder<K, V> name(String name);

    ICacheBuilder<K, V> keyMapper(Function<K, ObjectKey> keyMapper);

    ICacheBuilder<K, V> provider(Function<K, V> provider);

    ICacheBuilder<K, V> expire(int seconds);

    default ICacheBuilder<K, V> expire(long seconds)
    {
        this.expire((int) seconds);
        return this;
    }

    Cache<K, V> build();
}
