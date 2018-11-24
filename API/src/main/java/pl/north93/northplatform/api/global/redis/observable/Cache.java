package pl.north93.northplatform.api.global.redis.observable;

import javax.annotation.Nonnull;

import java.util.Optional;

public interface Cache<K, V>
{
    int size();

    boolean isEmpty();

    void put(K key, V value);

    Optional<V> get(K key);

    @Nonnull
    Value<V> getValue(K key);

    void clear();

    boolean contains(K key);

    void remove(K key);
}
