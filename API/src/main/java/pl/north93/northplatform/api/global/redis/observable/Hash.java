package pl.north93.northplatform.api.global.redis.observable;

import java.util.Set;

public interface Hash<V>
{
    String getName();

    /**
     * @param key Field name in hash.
     * @param value New value of field.
     * @return True if field doesn't exist and value has been set,
     *         false when field already exist and value has been updated.
     */
    boolean put(String key, V value);

    V get(String key);

    Value<V> getAsValue(String key);

    Set<String> keys();

    Set<V> values();

    /**
     * @param key Field name in hash.
     * @return True if successfully deleted.
     */
    boolean delete(String key);

    boolean exists(String key);

    long size();

    void clear();
}
