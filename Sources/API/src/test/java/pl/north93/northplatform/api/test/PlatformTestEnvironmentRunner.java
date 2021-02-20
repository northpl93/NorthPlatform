package pl.north93.northplatform.api.test;

import static pl.north93.northplatform.api.standalone.StandaloneHostConnector.STANDALONE_HOST;


import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.Platform;

/**
 * Prosta klasa umożliwiająca uruchomienie API
 * na platformie {@link Platform#STANDALONE}
 * w celach testowych.
 * <p>
 * W trybie testowym niedostępny jest klient redefiniowania kodu
 * więc automatyczne injekcie nie działają przez {@code new}
 */
public class PlatformTestEnvironmentRunner
{
    private static ApiCore testEnvironment;

    public static ApiCore ensureEnvironment()
    {
        if (testEnvironment != null)
        {
            return testEnvironment;
        }

        System.out.println("North API is starting test environment");

        final ApiCore apiCore = new ApiCore(STANDALONE_HOST, new TestHostConnector());
        testEnvironment = apiCore;

        apiCore.startPlatform();
        return apiCore;
    }

    public static void cleanupEnvironment()
    {
        if (testEnvironment == null)
        {
            return;
        }

        testEnvironment.stopPlatform();
        testEnvironment = null;
    }
}
