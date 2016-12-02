package pl.north93.zgame.api.global;

import static pl.north93.zgame.api.global.exceptions.SingletonException.checkSingleton;


import java.io.File;
import java.util.logging.Logger;

import pl.north93.zgame.api.global.exceptions.SingletonException;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.rpc.RpcManager;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriber;

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

    public static void runDebug(final Runnable runnable)
    {
        apiCore.runDebug(runnable);
    }

    public static TemplateManager getMessagePackTemplates()
    {
        return apiCore.getMessagePackTemplates();
    }

    public static INetworkManager getNetworkManager()
    {
        return apiCore.getNetworkManager();
    }

    public static RedisSubscriber getRedisSubscriber()
    {
        return apiCore.getRedisSubscriber();
    }

    @Deprecated
    public static RpcManager getRpcManager()
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
}
