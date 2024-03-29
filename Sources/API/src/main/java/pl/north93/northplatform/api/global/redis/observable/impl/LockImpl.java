package pl.north93.northplatform.api.global.redis.observable.impl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.redis.observable.Lock;

@Slf4j
class LockImpl implements Lock
{
    private final LockManagement lockManagement;
    private final String name;
    private final Object waiter;

    public LockImpl(final LockManagement lockManagement, final String name)
    {
        this.lockManagement = lockManagement;
        this.name = name;
        this.waiter = new Object();
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public LockImpl lock()
    {
        if (this.tryLock0())
        {
            log.debug("[Lock] Successfully acquired lock {}", this.name);
        }
        else
        {
            this.lockManagement.addWaitingLock(this);
            log.debug("[Lock] Lock {} is waiting...", this.name);
            while (! this.tryLock0())
            {
                this.awaitUnlockOrTimeout();
            }
            log.debug("[Lock] Successfully acquired lock {}", this.name);
        }
        return this;
    }

    @Override
    public boolean tryLock()
    {
        if (this.tryLock0())
        {
            log.debug("[Lock] Successfully acquired lock {}", this.name);
            return true;
        }
        return false;
    }

    private synchronized boolean tryLock0()
    {
        final long threadId = this.getVmThreadIdentifier();

        return this.lockManagement.lock(this.name, threadId);
    }

    @Override
    public void unlock()
    {
        if (! this.tryUnlock())
        {
            throw new RuntimeException("Failed to unlock " + this.name);
        }
        log.debug("[Lock] Successfully unlocked {}", this.name);
    }

    private synchronized boolean tryUnlock()
    {
        final long threadId = this.getVmThreadIdentifier();

        final int result = this.lockManagement.unlock(this.name, threadId);
        switch (result)
        {
            case 1:
                this.lockManagement.removeWaitingLock(this);
                this.notifyAllLocalWaiters();

                return true;
            default:
                return false;
        }
    }

    /*default*/ synchronized void remoteUnlock()
    {
        this.notifyAllLocalWaiters();

        log.debug("[Lock] Remote unlock {}", this.name);
    }

    private void notifyAllLocalWaiters()
    {
        synchronized (this.waiter)
        {
            this.waiter.notifyAll();
        }
    }

    private void awaitUnlockOrTimeout()
    {
        try
        {
            synchronized (this.waiter)
            {
                this.waiter.wait(TimeUnit.SECONDS.toMillis(1)); // after 1 second time out
            }
        }
        catch (final InterruptedException e)
        {
            log.error("awaitUnloadOrTimeout interrupted", e);
        }
    }

    private long getVmThreadIdentifier()
    {
        final long processId = ProcessHandle.current().pid();
        final long threadId = Thread.currentThread().getId();

        return processId * 1000 + threadId;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).toString();
    }
}