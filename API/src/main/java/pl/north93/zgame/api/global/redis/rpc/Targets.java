package pl.north93.zgame.api.global.redis.rpc;

public final class Targets
{
    public static RpcTarget proxy(final String proxyId)
    {
        return new RpcCustomTarget("proxy:" + proxyId);
    }

    public static RpcTarget daemon(final String daemonName)
    {
        return new RpcCustomTarget("daemon:" + daemonName);
    }

    public static RpcTarget networkController()
    {
        return new RpcCustomTarget("controller");
    }
}
