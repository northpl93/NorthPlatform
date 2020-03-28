package pl.north93.northplatform.api.global.redis.observable;

public interface IObservationManager
{
    <T> Value<T> get(Class<T> clazz, String objectKey);

    <T> Value<T> get(Class<T> clazz, ObjectKey objectKey);

    <T> Value<T> get(Hash<T> hash, String key); // tworzy Value z klucza hasha

    <K, V> ICacheBuilder<K, V> cacheBuilder(Class<K> keyClass, Class<V> valueClass);

    /**
     * Tworzy locka opartego o Redisa o podanej nazwie.
     * Lock sie wazny przez maksymalnie 30 sekund, po
     * tym czasie zostanie automatycznie zunlockowany.
     *
     * Aby ten mechanizm dzialal w pelni potrzebne jest wlaczenie
     * notifications w redisie. Wymagane minimum to Ex.
     *
     * CONFIG SET notify-keyspace-events Ex
     *
     * @param name nazwa locka (nazwa klucza w redisie).
     * @return obiekt locka.
     */
    Lock getLock(String name);

    Lock getMultiLock(String... names);

    Lock getMultiLock(Lock... locks);

    <K> SortedSet<K> getSortedSet(String name);

    <V> Hash<V> getHash(Class<V> valueClass, String name);
}
