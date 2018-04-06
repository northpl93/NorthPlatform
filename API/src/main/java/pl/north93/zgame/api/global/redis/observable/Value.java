package pl.north93.zgame.api.global.redis.observable;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
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
     * Returns value content as {@link Optional}.
     * @return value content as optional.
     */
    default Optional<T> getOptional()
    {
        return Optional.ofNullable(this.get());
    }

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

    /**
     * Sets new value, upload it to Redis and set expire.
     * @param newValue new value which will be uploaded to redis.
     */
    void setExpire(T newValue, long time, TimeUnit timeUnit);

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
     * Checks if value is preset using cache and redis.
     * @return True if value is preset.
     */
    default boolean isPreset()
    {
        return this.isCached() || this.isAvailable();
    }

    /**
     * Set a timeout on key.
     * Pass here -1 to persist (remove timeout) value.
     *
     * @param seconds time of key liveness, or -1 to remove timeout.
     * @return True if expire successfully changed/removed.
     *         False if value doesn't exists or expire remove failed.
     */
    boolean expire(int seconds);

    /**
     * Returns TTL of this key.
     * Returns -1 if it's infinity.
     * @return TTL of this key.
     */
    long getTimeToLive();

    /**
     * Returns Lock instance associated with this value.
     * @return Lock associated with this value.
     */
    Lock getLock();

    /**
     * Acquires lock and returns it's instance.
     * @see Lock#lock()
     * @return Instance of lock in locked state.
     */
    Lock lock();
}
