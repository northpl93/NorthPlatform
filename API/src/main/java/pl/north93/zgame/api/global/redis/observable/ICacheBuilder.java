package pl.north93.zgame.api.global.redis.observable;

import java.util.function.Function;

public interface ICacheBuilder<K, V>
{
    ICacheBuilder<K, V> name(String name);

    ICacheBuilder<K, V> keyMapper(Function<K, ObjectKey> keyMapper);

    ICacheBuilder<K, V> provider(Function<K, V> provider);

    Cache<K, V> build();
}
