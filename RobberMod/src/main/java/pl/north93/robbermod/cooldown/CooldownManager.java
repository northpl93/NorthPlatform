package pl.north93.robbermod.cooldown;

import java.util.Collection;
import java.util.Set;

/**
 * Represent cooldown manager. <br>
 * Cooldowns may be checked only at access time, you can remove old values by {@link #getExpired()} method.
 */
public interface CooldownManager<K>
{
    /**
     * Adds new object to manager with given cooldown time. <br>
     * If object is already in manager cooldown time will be restarted and replaced with new one.
     *
     * @param key          key to be added.
     * @param cooldownTime time of cooldown.
     *
     * @return created cooldown entry.
     */
    default CooldownEntry<K> add(final K key, final long cooldownTime)
    {
        return this.add(key, cooldownTime, System.currentTimeMillis());
    }

    /**
     * Adds new object to manager with given cooldown time.
     *
     * @param key          key to be added.
     * @param cooldownTime time of cooldown.
     * @param from         time to use as current time. (in milliseconds)
     */
    CooldownEntry<K> add(K key, long cooldownTime, long from);

    /**
     * Removes (cancel cooldown) given key from manager.
     *
     * @param key key to cancel cooldown.
     *
     * @return removed cooldown entry or null if there was no entry for given key.
     */
    CooldownEntry<K> remove(K key);

    /**
     * Returns true if given key isn't in manager or cooldown already expired. <br>
     * If cooldown will be expired at moment of check, it will be removed from manager.
     *
     * @param key key to be checked.
     *
     * @return true if given key isn't in manager or cooldown already expired.
     */
    default boolean hasExpired(final K key)
    {
        return this.hasExpired(key, System.currentTimeMillis());
    }

    /**
     * Returns true if given key isn't in manager or cooldown already expired. <br>
     * If cooldown will be expired at moment of check, it will be removed from manager.
     *
     * @param key  key to be checked.
     * @param from time to use as current time. (in milliseconds)
     *
     * @return true if given key isn't in manager or cooldown already expired.
     */
    default boolean hasExpired(final K key, final long from)
    {
        final CooldownEntry<K> entry = this.getEntry(key);
        return (entry == null) || entry.hasExpired();
    }

    /**
     * Returns true if given key isn't in manager or cooldown already expired. <br>
     * If cooldown has expired (or is missing), new cooldown will be set.
     *
     * @param key          key to be checked/added.
     * @param cooldownTime time of cooldown.
     *
     * @return true if given key isn't in manager or cooldown already expired.
     */
    default boolean hasExpiredOrAdd(final K key, final long cooldownTime)
    {
        return this.hasExpiredOrAdd(key, cooldownTime, System.currentTimeMillis());
    }

    /**
     * Returns true if given key isn't in manager or cooldown already expired. <br>
     * If cooldown has expired (or is missing), new cooldown will be set.
     *
     * @param key          key to be checked/added.
     * @param cooldownTime time of cooldown.
     * @param from         time to use as current time. (in milliseconds)
     *
     * @return true if given key isn't in manager or cooldown already expired.
     */
    default boolean hasExpiredOrAdd(final K key, final long cooldownTime, final long from)
    {
        if (! this.hasExpired(key, from))
        {
            return false;
        }
        this.add(key, cooldownTime, from);
        return true;
    }

    /**
     * Returns cooldown entry for given key, or null if entry don't exist. <br>
     * If cooldown will be expired at moment of check, it will be removed from manager, but entry will be still returned.
     *
     * @param key key to check.
     *
     * @return cooldown entry for given key, or null if entry don't exist.
     */
    CooldownEntry<K> getEntry(final K key);

    /**
     * Returns cooldown entry for given key, or create new one if entry don't exist. <br>
     *
     * @param key          key to check.
     * @param cooldownTime time of cooldown.
     *
     * @return cooldown entry for given key.
     */
    default CooldownEntry<K> getOrCreateEntry(final K key, final long cooldownTime)
    {
        return this.getOrCreateEntry(key, cooldownTime, System.currentTimeMillis());
    }

    /**
     * Returns cooldown entry for given key, or create new one if entry don't exist. <br>
     *
     * @param key          key to check.
     * @param cooldownTime time of cooldown.
     * @param from         time to use as current time. (in milliseconds)
     *
     * @return cooldown entry for given key.
     */
    default CooldownEntry<K> getOrCreateEntry(final K key, final long cooldownTime, final long from)
    {
        final CooldownEntry<K> entry = this.getEntry(key);
        if (entry == null)
        {
            return this.add(key, cooldownTime, from);
        }
        return entry;
    }

    /**
     * Check all entries if they are expired, and remove them from manager.
     *
     * @return all removed entires.
     */
    default Set<? extends CooldownEntry<K>> getExpired()
    {
        return this.getExpired(System.currentTimeMillis());
    }

    /**
     * Check all entries if they are expired, and remove them from manager.
     *
     * @param from time to use as current time. (in milliseconds)
     *
     * @return all removed entires.
     */
    Set<? extends CooldownEntry<K>> getExpired(long from);

    /**
     * Check all entries if they are expired, and remove them from manager.
     *
     * @return true if any element was removed.
     */
    default boolean removeExpired()
    {
        return this.removeExpired(System.currentTimeMillis());
    }

    /**
     * Check all entries if they are expired, and remove them from manager.
     *
     * @param from time to use as current time. (in milliseconds)
     *
     * @return true if any element was removed.
     */
    default boolean removeExpired(final long from)
    {
        return ! this.getExpired(from).isEmpty();
    }

    /**
     * Returns true if there is no entires in this manager.
     *
     * @return true if there is no entires in this manager.
     */
    default boolean isEmpty()
    {
        return this.getEntires().isEmpty();
    }

    /**
     * Removes all entries from manager.
     */
    void clear();

    /**
     * Returns all entires without removing expired one.
     *
     * @return all entires without removing expired one.
     */
    Collection<? extends CooldownEntry<K>> getEntires();

    /**
     * Create and return defult, basic (concurrent) implementation of cooldown manager.
     *
     * @param initialSize initial size of backing hashmap.
     * @param <K>         type of keys.
     *
     * @return created defult, basic implementation of cooldown manager.
     */
    static <K> CooldownManager<K> createManager(final int initialSize)
    {
        return new BasicCooldownManager<>(initialSize);
    }

    /**
     * Create and return defult, basic (concurrent) implementation of cooldown manager.
     *
     * @param clazz       clazz defining type of manager keys, used only as generic type selector.
     * @param initialSize initial size of backing hashmap.
     * @param <K>         type of keys.
     *
     * @return created defult, basic implementation of cooldown manager.
     */
    static <K> CooldownManager<K> createManager(final Class<K> clazz, final int initialSize)
    {
        return createManager(initialSize);
    }

}
