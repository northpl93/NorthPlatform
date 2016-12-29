package pl.north93.zgame.api.global.redis.rpc;

import static pl.north93.zgame.api.global.redis.RedisKeys.DAEMON;
import static pl.north93.zgame.api.global.redis.RedisKeys.PROXY_INSTANCE;
import static pl.north93.zgame.api.global.redis.RedisKeys.SERVER;


import java.util.UUID;

public final class Targets
{
    public static IRpcTarget server(final UUID serverId)
    {
        return new RpcCustomTarget(SERVER + serverId);
    }

    public static IRpcTarget proxy(final String proxyId)
    {
        return new RpcCustomTarget(PROXY_INSTANCE + proxyId);
    }

    public static IRpcTarget daemon(final String daemonName)
    {
        return new RpcCustomTarget(DAEMON + daemonName);
    }

    public static IRpcTarget networkController()
    {
        return new RpcCustomTarget("controller");
    }
}
