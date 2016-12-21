package pl.north93.zgame.api.global.redis.observable;

import java.util.function.Consumer;

public interface Value<T>
{
    /**
     * Returns value content.
     * @return value content.
     */
    T get();

    /**
     * Asynchronously gets value.
     * @param callback Callback.
     */
    void get(Consumer<T> callback);

    /**
     * Sets new value and upload it to Redis.
     * @param newValue new value which will be uploaded to redis.
     */
    void set(T newValue);

    /**
     * Uploads current cached value to redis.
     */
    void upload();

    /**
     * Deletes this value from redis and local cache.
     */
    void delete();

    /**
     * Checks if this value is available in redis.
     * @return true if value is available.
     */
    boolean isAvailable();

    /**
     * Checks if there is cached value.
     * @return true if is local cached value.
     */
    boolean isCached();
}
