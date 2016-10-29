package pl.north93.zgame.api.global.redis.rpc.exceptions;

public class RpcTimeoutException extends RpcException
{
    public RpcTimeoutException()
    {
        super("A timeout while executing RPC method.");
    }
}
