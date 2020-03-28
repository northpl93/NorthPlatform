package pl.north93.northplatform.api.global.redis.rpc.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.ToString;
import pl.north93.northplatform.api.global.redis.rpc.exceptions.RpcTimeoutException;

@ToString
class RpcResponseLock
{
    private final CompletableFuture<Object> future = new CompletableFuture<>();

    public Object getResponse(final int timeout) throws Exception
    {
        final Object result;
        try
        {
            result = this.future.get(timeout, TimeUnit.MILLISECONDS);
        }
        catch (final TimeoutException exception)
        {
            throw new RpcTimeoutException(); // response has not been provided after some time
        }
        return result;
    }

    public void provideResponse(final Object response)
    {
        this.future.complete(response);
    }
}
