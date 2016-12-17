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

    void set(T newValue);

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
