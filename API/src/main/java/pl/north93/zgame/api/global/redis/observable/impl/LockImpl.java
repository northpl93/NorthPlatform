package pl.north93.zgame.api.global.redis.observable.impl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.observable.Lock;
import redis.clients.jedis.Jedis;

class LockImpl implements Lock
{
    private final ObservationManagerImpl observationManager;
    private final String                 name;
    private final Object                 waiter;
    private boolean isLockedLocally;
    private volatile boolean isWaiting;

    public LockImpl(final ObservationManagerImpl observationManager, final String name)
    {
        this.observationManager = observationManager;
        this.name = name;
        this.waiter = new Object();
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void lock()
    {
        if (this.tryLock0())
        {
            this.isLockedLocally = true;
        }
        else
        {
            this.isWaiting = true;
            this.observationManager.addWaitingLock(this);
            try
            {
                synchronized (this.waiter)
                {
                    this.waiter.wait(TimeUnit.SECONDS.toMillis(2)); // after 2 seconds time out
                }
            }
            catch (final InterruptedException e)
            {
                e.printStackTrace();
            }
            this.isWaiting = false;
            this.lock(); // next try
        }
    }

    @Override
    public boolean tryLock()
    {
        if (this.tryLock0())
        {
            this.isLockedLocally = true;
            return true;
        }
        return false;
    }

    private synchronized boolean tryLock0()
    {
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            return ((long) jedis.eval("if (redis.call('exists', KEYS[1]) == 1) then\n" +
                               "return 0\n" +
                               "else\n" +
                               "redis.call('set', KEYS[1], 1)\n" +
                               "return 1\n" +
                               "end\n", 1, this.name)) == 1;
        }
    }

    @Override
    public synchronized void unlock()
    {
        if (! this.isLockedLocally)
        {
            throw new IllegalStateException("Lock must be locked locally!");
        }
        if (! this.tryUnlock())
        {
            throw new RuntimeException("Failed to unlock " + this.name);
        }
        this.isLockedLocally = false;
    }

    private boolean tryUnlock()
    {
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            return ((long) jedis.eval("if (redis.call('del', KEYS[1]) == 1) then\n" +
                                             "redis.call('publish', \"unlock\", KEYS[1])\n" +
                                             "return 1\n" +
                                             "else\n" +
                                             "return 0\n" +
                                             "end", 1, this.name)) == 1;
        }
    }

    /*default*/ void remoteUnlock()
    {
        if (this.isWaiting)
        {
            synchronized (this.waiter)
            {
                this.waiter.notifyAll();
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("isLockedLocally", this.isLockedLocally).append("isWaiting", this.isWaiting).toString();
    }
}
