package pl.north93.zgame.api.global.redis.rpc.impl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.rpc.exceptions.RpcTimeoutException;

public class RpcResponseLock
{
    //private final CountDownLatch lock = new CountDownLatch(1);
    private final Object lock = new Object();
    private       Object response;

    public Object getResponse(final int timeout) throws Exception
    {
        //this.lock.await(timeout, TimeUnit.SECONDS);
        synchronized (this.lock)
        {
            this.lock.wait(TimeUnit.SECONDS.toMillis(timeout));
        }
        if (this.response == null)
        {
            throw new RpcTimeoutException(); // response is null after some time
        }
        return this.response;
    }

    public boolean isResponseProvided()
    {
        return this.response != null;
    }

    /*default*/ void provideResponse(final Object response)
    {
        this.response = response;
        synchronized (this.lock)
        {
            this.lock.notifyAll();
        }
        //this.lock.countDown();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("lock", this.lock).append("response", this.response).toString();
    }
}
