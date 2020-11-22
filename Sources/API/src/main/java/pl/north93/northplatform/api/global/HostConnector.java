package pl.north93.northplatform.api.global;

import java.io.File;

/**
 * Manages connection between NorthPlatform and software that hosts NorthPlatform (e.g. Bukkit, Bungee)
 */
public interface HostConnector
{
    String onPlatformInit(ApiCore apiCore);

    void onPlatformStart(ApiCore apiCore);

    /**
     * Cleanups NorthPlatform resources when host software is shutting down.
     */
    void onPlatformStop(ApiCore apiCore);

    File getRootDirectory();

    File getFile(String name);

    /**
     * Calls host software to begin shutdown procedure.
     */
    void shutdownHost();

    void runTaskAsynchronously(Runnable runnable);

    void runTaskAsynchronously(Runnable runnable, int ticks);
}
