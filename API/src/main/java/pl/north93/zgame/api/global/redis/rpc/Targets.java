package pl.north93.zgame.api.global.redis.rpc;

public final class Targets
{
    public static IRpcTarget proxy(final String proxyId)
    {
        return new RpcCustomTarget("proxy:" + proxyId);
    }

    public static IRpcTarget daemon(final String daemonName)
    {
        return new RpcCustomTarget("daemon:" + daemonName);
    }

    public static IRpcTarget networkController()
    {
        return new RpcCustomTarget("controller");
    }
}
