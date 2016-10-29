package pl.north93.robbermod.cooldown;

/**
 * Represent cooldown entry, entry contains used key, cooldown time, and start time (cooldown is counted relative to start time).
 *
 * @param <K> type of key.
 */
public interface CooldownEntry<K>
{
    /**
     * Returns key of this cooldown entry.
     *
     * @return key of this cooldown entry.
     */
    K getKey();

    /**
     * Returns start time of this cooldown. (milliseconds)
     *
     * @return start time of this cooldown. (milliseconds)
     */
    long getStartTime();

    /**
     * Returns time of this cooldown. (milliseconds)
     *
     * @return time of this cooldown. (milliseconds)
     */
    long getCooldownTime();

    /**
     * Returns delta time of cooldown - rest of cooldown time if it already started. <br>
     * If cooldown isn't started yet, returned value may be higher than cooldown time. <br>
     * This method will remove entry from manager if it is expired.
     *
     * @return delta time of cooldown - rest of cooldown time if it already started.
     */
    default long delta()
    {
        return this.delta(System.currentTimeMillis());
    }

    /**
     * Returns delta time of cooldown - rest of cooldown time if it already started. <br>
     * If cooldown isn't started yet, returned value may be higher than cooldown time. <br>
     * This method will remove entry from manager if it is expired.
     *
     * @param currentTime time in milliseconds that will be used as current one.
     *
     * @return delta time of cooldown - rest of cooldown time if it already started.
     */
    default long delta(final long currentTime)
    {
        final long sum = this.getStartTime() + this.getCooldownTime();
        return sum - currentTime;
    }

    /**
     * Returns true if this cooldown already started. <br>
     * This method will remove entry from manager if it is expired.
     *
     * @return true if this cooldown already started.
     */
    default boolean hasAlreadyStarted()
    {
        return this.hasAlreadyStarted(System.currentTimeMillis());
    }

    /**
     * Returns true if this cooldown already started. <br>
     * This method will remove entry from manager if it is expired.
     *
     * @param currentTime time in milliseconds that will be used as current one.
     *
     * @return true if this cooldown already started.
     */
    default boolean hasAlreadyStarted(final long currentTime)
    {
        return this.delta(currentTime) <= this.getCooldownTime();
    }

    /**
     * Returns true if cooldown time already expired. <br>
     * This method will remove entry from manager if it is expired.
     *
     * @return true if cooldown time already expired.
     */
    default boolean hasExpired()
    {
        return this.hasExpired(System.currentTimeMillis());
    }

    /**
     * Returns true if cooldown time already expired. <br>
     * This method will remove entry from manager if it is expired.
     *
     * @param currentTime time in milliseconds that will be used as current one.
     *
     * @return true if cooldown time already expired.
     */
    default boolean hasExpired(final long currentTime)
    {
        return this.delta(currentTime) <= 0;
    }

    /**
     * Returns delta time of cooldown - rest of cooldown time if it already started. <br>
     * If cooldown isn't started yet, returned value may be higher than cooldown time.
     *
     * @return delta time of cooldown - rest of cooldown time if it already started.
     */
    default long deltaLazy()
    {
        return this.deltaLazy(System.currentTimeMillis());
    }

    /**
     * Returns delta time of cooldown - rest of cooldown time if it already started. <br>
     * If cooldown isn't started yet, returned value may be higher than cooldown time.
     *
     * @param currentTime time in milliseconds that will be used as current one.
     *
     * @return delta time of cooldown - rest of cooldown time if it already started.
     */
    default long deltaLazy(final long currentTime)
    {
        final long sum = this.getStartTime() + this.getCooldownTime();
        return sum - currentTime;
    }

    /**
     * Returns true if this cooldown already started.
     *
     * @return true if this cooldown already started.
     */
    default boolean hasAlreadyStartedLazy()
    {
        return this.hasAlreadyStartedLazy(System.currentTimeMillis());
    }

    /**
     * Returns true if this cooldown already started.
     *
     * @param currentTime time in milliseconds that will be used as current one.
     *
     * @return true if this cooldown already started.
     */
    default boolean hasAlreadyStartedLazy(final long currentTime)
    {
        return this.deltaLazy(currentTime) <= this.getCooldownTime();
    }

    /**
     * Returns true if cooldown time already expired.
     *
     * @return true if cooldown time already expired.
     */
    default boolean hasExpiredLazy()
    {
        return this.hasExpiredLazy(System.currentTimeMillis());
    }

    /**
     * Returns true if cooldown time already expired.
     *
     * @param currentTime time in milliseconds that will be used as current one.
     *
     * @return true if cooldown time already expired.
     */
    default boolean hasExpiredLazy(final long currentTime)
    {
        return this.deltaLazy(currentTime) <= 0;
    }
}
