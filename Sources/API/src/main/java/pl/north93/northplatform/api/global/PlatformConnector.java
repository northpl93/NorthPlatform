package pl.north93.northplatform.api.global;

public interface PlatformConnector
{
    /**
     * Wyłącza serwer.
     */
    void stop();

    void runTaskAsynchronously(Runnable runnable);

    void runTaskAsynchronously(Runnable runnable, int ticks);
}
