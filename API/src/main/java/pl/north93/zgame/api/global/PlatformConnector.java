package pl.north93.zgame.api.global;

public interface PlatformConnector
{
    /**
     * Wyłącza serwer.
     */
    void stop();

    void runTaskAsynchronously(Runnable runnable);

    void runTaskAsynchronously(Runnable runnable, int ticks);
}
