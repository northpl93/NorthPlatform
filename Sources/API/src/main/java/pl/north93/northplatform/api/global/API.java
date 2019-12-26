package pl.north93.northplatform.api.global;

import static pl.north93.northplatform.api.global.utils.exceptions.SingletonException.checkSingleton;

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
}
