package pl.north93.zgame.api.global.redis.rpc.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.rpc.exceptions.RpcTimeoutException;

class RpcResponseLock
{
    private final CompletableFuture<Object> future = new CompletableFuture<>();

    public Object getResponse(final int timeout) throws Exception
    {
        final Object result = this.future.get(timeout, TimeUnit.MILLISECONDS);
        if (! this.future.isDone())
        {
            throw new RpcTimeoutException(); // response is not provided after some time
        }
        return result;
    }

    public CompletableFuture<Object> getFuture()
    {
        return this.future;
    }

    public boolean isResponseProvided()
    {
        return this.future.isDone();
    }

    /*default*/ void provideResponse(final Object response)
    {
        this.future.complete(response);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("future", this.future).toString();
    }
}
