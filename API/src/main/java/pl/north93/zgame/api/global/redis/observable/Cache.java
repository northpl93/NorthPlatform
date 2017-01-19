package pl.north93.zgame.api.global.redis.observable;

public interface Cache<K, V>
{
    int size();

    boolean isEmpty();

    void put(K key, V value);

    V get(K key);

    Value<V> getValue(K key);

    void clear();

    boolean contains(K key);

    void remove(K key);
}
