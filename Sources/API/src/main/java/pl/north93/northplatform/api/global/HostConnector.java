package pl.north93.northplatform.api.global;

import java.io.File;

/**
 * Manages connection between NorthPlatform and software that hosts NorthPlatform (e.g. Bukkit, Bungee)
 */
public interface HostConnector
{
    /**
     * Handles early (before components) platform initialisation.
     * Should return unique identifier of this NorthPlatform instance.
     *
     * @param apiCore Instance of NorthPlatform.
     * @return ID of this instance.
     */
    String onPlatformInit(ApiCore apiCore);

    /**
     * Handles late (after components) platform initialisation.
     *
     * @param apiCore Instance of NorthPlatform.
     */
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
