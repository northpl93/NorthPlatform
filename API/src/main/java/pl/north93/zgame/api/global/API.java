package pl.north93.zgame.api.global;

import static pl.north93.zgame.api.global.exceptions.SingletonException.checkSingleton;


import java.util.logging.Logger;

import pl.north93.zgame.api.global.component.annotations.ProvidesComponent;
import pl.north93.zgame.api.global.network.INetworkManager;

public final class API
{
    private static ApiCore apiCore;

    public static void setApiCore(final ApiCore newApi)
    {
        checkSingleton(apiCore, "apiCore");
        API.apiCore = newApi;
    }

    public static ApiCore getApiCore()
    {
        return API.apiCore;
    }

    @ProvidesComponent
    public static INetworkManager getNetworkManager()
    {
        return apiCore.getNetworkManager();
    }

    public static Logger getLogger()
    {
        return apiCore.getLogger();
    }
}
