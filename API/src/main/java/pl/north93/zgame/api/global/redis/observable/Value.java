package pl.north93.zgame.api.global.redis.observable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Value<T>
{
    /**
     * Returns value content.
     * @return value content.
     */
    T get();

    /**
     * Returns value content ignoring cache.
     * @return value content.
     */
    T getWithoutCache();

    /**
     * Returns value content if this is in redis.
     * Otherwise it's obtained from provided supplier and uploaded to Redis.
     *
     * @param defaultValue Value which will be obtained when it doesn't exists in redis.
     * @return value or default value.
     */
    T getOr(Supplier<T> defaultValue);

    /**
     * Stomically get and delete key.
     * It skips cache.
     *
     * @return value of that key, or null.
     */
    T getAndDelete();

    boolean update(Function<T, T> update);

    default boolean update(Consumer<T> value)
    {
        return this.update(remote -> {
            value.accept(remote);
            return remote;
        });
    }

    /**
     * Asynchronously gets value.
     * @param callback Callback.
     */
    void get(Consumer<T> callback);

    /**
     * Synchronously performs specified action if value is present.
     * @param action Action to perform if value is present.
     */
    void ifPresent(Consumer<T> action);

    /**
     * Sets new value and upload it to Redis.
     * @param newValue new value which will be uploaded to redis.
     */
    void set(T newValue);

    Value<T> setIfUnavailable(Supplier<T> defaultValue);

    /**
     * Deletes this value from redis and local cache.
     */
    boolean delete();

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

    /**
     * Set a timeout on key.
     * @param seconds time of key liveness
     */
    void expire(int seconds);

    /**
     * Returns TTL of this key.
     * Returns -1 if it's infinity.
     * @return TTL of this key.
     */
    long getTimeToLive();

    Lock getLock();

    void lock();

    void unlock();
}
