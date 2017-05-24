package pl.north93.zgame.api.global.redis.observable;

public interface IObservationManager
{
    <T> Value<T> get(Class<T> clazz, String objectKey);

    <T> Value<T> get(Class<T> clazz, ObjectKey objectKey);

    <T> Value<T> get(Class<T> clazz, ProvidingRedisKey keyProvider);

    <T> Value<T> get(Hash<T> hash, String key); // tworzy Value z klucza hasha

    <T extends ProvidingRedisKey> Value<T> of(T preCachedObject);

    <K, V> ICacheBuilder<K, V> cacheBuilder(Class<K> keyClass, Class<V> valueClass);

    Lock getLock(String name);

    Lock getMultiLock(String... names);

    <K> SortedSet<K> getSortedSet(String name);

    <V> Hash<V> getHash(Class<V> valueClass, String name);
}
