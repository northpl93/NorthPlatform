package pl.north93.northplatform.api.standalone;

import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.Platform;

public class StandaloneApiCore
{
    public static void main(final String... args)
    {
        System.out.println("NorthPlatform API is running as standalone application");
        final ApiCore apiCore = new ApiCore(Platform.STANDALONE, new StandaloneHostConnector());
        apiCore.startPlatform();
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            if (apiCore.getApiState().isDisabled())
            {
                return;
            }
            apiCore.stopPlatform();
        }));
    }
}
