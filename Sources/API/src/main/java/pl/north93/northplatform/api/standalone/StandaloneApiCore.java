package pl.north93.northplatform.api.standalone;

import static pl.north93.northplatform.api.standalone.StandaloneHostConnector.STANDALONE_HOST;


import pl.north93.northplatform.api.global.ApiCore;

public class StandaloneApiCore
{
    public static void main(final String... args)
    {
        System.out.println("NorthPlatform API is running as standalone application");
        final ApiCore apiCore = new ApiCore(STANDALONE_HOST, new StandaloneHostConnector());
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
