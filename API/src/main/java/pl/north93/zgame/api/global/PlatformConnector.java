package pl.north93.zgame.api.global;

public interface PlatformConnector
{
    /**
     * Wyłącza serwer.
     */
    void stop();

    /**
     * Wyrzuca wszystkich graczy z serwera, jeśli to obsługiwane.
     */
    void kickAll();

    void runTaskAsynchronously(Runnable runnable);

    void runTaskAsynchronously(Runnable runnable, int ticks);
}
