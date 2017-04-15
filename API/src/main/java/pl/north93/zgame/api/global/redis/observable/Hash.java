package pl.north93.zgame.api.global.redis.observable;

import java.util.Set;

public interface Hash<V>
{
    void put(String key, V value);

    V get(String key);

    Set<String> keys();

    Set<V> values();

    void delete(String key);

    boolean exists(String key);

    long size();

    void clear();
}
