package pl.north93.zgame.api.global.redis.observable;

public interface Lock
{
    String getName();

    /**
     * Blokuje. Jeśli ktoś inny już zablokował to czekamy...
     */
    void lock();

    /**
     * Próbuje zlockować.
     * Jeśli się uda zwraca true, jeśli nie - false.
     *
     * @return czy udało się zlockować.
     */
    boolean tryLock();

    /**
     * Odblokowuje.
     */
    void unlock();
}
