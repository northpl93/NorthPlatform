package pl.north93.northplatform.api.global.redis.rpc.exceptions;

import pl.north93.northplatform.api.global.redis.rpc.impl.messaging.RpcExceptionInfo;

public class RpcRemoteException extends RpcException
{
    public RpcRemoteException(final RpcExceptionInfo exceptionInfo)
    {
        super("An exception of type " + exceptionInfo.getExceptionClass() + " has been thrown on remote. Message: " + exceptionInfo.getMessage());
    }
}
