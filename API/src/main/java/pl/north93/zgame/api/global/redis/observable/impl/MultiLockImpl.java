package pl.north93.zgame.api.global.redis.observable.impl;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.observable.Lock;

class MultiLockImpl implements Lock
{
    private final Object localLock;
    private final Lock[] locks;

    MultiLockImpl(final Lock[] locks)
    {
        this.localLock = new Object();
        this.locks = locks;
    }

    @Override
    public String getName()
    {
        return Arrays.stream(this.locks).map(Lock::getName).collect(Collectors.joining(","));
    }

    @Override
    public Lock lock()
    {
        synchronized (this.localLock)
        {
            for (final Lock lock : this.locks)
            {
                lock.lock();
            }
        }
        return this;
    }

    @Override
    public boolean tryLock()
    {
        throw new UnsupportedOperationException("tryLock on MultiLock");
    }

    @Override
    public void unlock()
    {
        synchronized (this.localLock)
        {
            for (final Lock lock : this.locks)
            {
                lock.unlock();
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("locks", this.locks).toString();
    }
}
