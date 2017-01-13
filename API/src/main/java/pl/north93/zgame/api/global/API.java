package pl.north93.zgame.api.global;

import static pl.north93.zgame.api.global.exceptions.SingletonException.checkSingleton;


import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import pl.north93.zgame.api.global.component.annotations.ProvidesComponent;
import pl.north93.zgame.api.global.exceptions.SingletonException;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;

public final class API
{
    private static ApiCore apiCore;

    public static void setApiCore(final ApiCore newApi) throws SingletonException
    {
        checkSingleton(apiCore, "apiCore");
        API.apiCore = newApi;
    }

    public static ApiCore getApiCore()
    {
        return API.apiCore;
    }

    public static void debug(final Object object)
    {
        apiCore.debug(object);
    }

    @ProvidesComponent
    public static INetworkManager getNetworkManager()
    {
        return apiCore.getNetworkManager();
    }

    @ProvidesComponent
    public static IRpcManager getRpcManager()
    {
        return apiCore.getRpcManager();
    }

    public static Platform getPlatform()
    {
        return apiCore.getPlatform();
    }

    public static PlatformConnector getPlatformConnector()
    {
        return apiCore.getPlatformConnector();
    }

    public static File getFile(final String name)
    {
        return apiCore.getFile(name);
    }

    public static Logger getLogger()
    {
        return apiCore.getLogger();
    }

    public static String message(final ResourceBundle resourceBundle, final String key, final Object... params)
    {
        return MessageFormat.format(resourceBundle.getString(key).replace('&', (char)167), params);
    }
}
