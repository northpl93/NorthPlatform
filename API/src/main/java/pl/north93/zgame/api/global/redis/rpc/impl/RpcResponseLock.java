package pl.north93.zgame.api.global.redis.rpc.impl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.rpc.exceptions.RpcTimeoutException;

public class RpcResponseLock
{
    private final CountDownLatch lock = new CountDownLatch(1);
    private       boolean        isProvided;
    private       Object         response;

    public Object getResponse(final int timeout) throws Exception
    {
        this.lock.await(timeout, TimeUnit.MILLISECONDS);
        if (! this.isProvided)
        {
            throw new RpcTimeoutException(); // response is not provided after some time
        }
        return this.response;
    }

    public boolean isResponseProvided()
    {
        return this.isProvided;
    }

    /*default*/ void provideResponse(final Object response)
    {
        this.response = response;
        this.isProvided = true;
        this.lock.countDown();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("isProvided", this.isProvided).append("response", this.response).toString();
    }
}
