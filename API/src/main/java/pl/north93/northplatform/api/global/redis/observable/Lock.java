package pl.north93.northplatform.api.global.redis.observable;

public interface Lock extends AutoCloseable
{
    String getName();

    /**
     * Blokuje. Jeśli ktoś inny już zablokował to czekamy...
     * <p>
     * Zwraca tą samą instancję po udanym założeniu locka.
     * Ułatwia to użycie kontrukcji try/catch-witch-resources.
     *
     * @return Zwraca instancję na której wywołano tą metodę. (czyli po prostu this)
     */
    Lock lock();

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

    /**
     * Wywołanie tej metody jest równoważne metodzie {@link #unlock()}.
     * Przydatne w try/catch-with-resources.
     *
     * @see #lock()
     */
    @Override
    default void close()
    {
        this.unlock();
    }
}
