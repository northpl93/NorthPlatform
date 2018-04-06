package pl.north93.zgame.api.global.redis.observable.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lambdaworks.redis.ScriptOutputType;
import com.lambdaworks.redis.api.sync.RedisCommands;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.observable.Lock;

class LockImpl implements Lock
{
    private final ObservationManagerImpl observationManager;
    private final String                 name;
    private final Object                 waiter;
    private final AtomicBoolean          isLockedLocally;

    public LockImpl(final ObservationManagerImpl observationManager, final String name)
    {
        this.observationManager = observationManager;
        this.name = name;
        this.waiter = new Object();
        this.isLockedLocally = new AtomicBoolean(false);
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public LockImpl lock()
    {
        final Logger logger = this.observationManager.getMyLogger();
        if (this.tryLock0())
        {
            this.isLockedLocally.compareAndSet(false, true);
            logger.log(Level.FINE, "[Lock] Successfully acquired lock {0}", this.name);
        }
        else
        {
            this.observationManager.addWaitingLock(this);
            logger.log(Level.FINE, "[Lock] Lock {0} is waiting...", this.name);
            while (! this.tryLock0())
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
                    e.printStackTrace();
                }
            }
            this.isLockedLocally.compareAndSet(false, true);
            logger.log(Level.FINE, "[Lock] Successfully acquired lock {0}", this.name);
        }
        return this;
    }

    @Override
    public boolean tryLock()
    {
        final Logger logger = this.observationManager.getMyLogger();
        if (this.tryLock0())
        {
            if (! this.isLockedLocally.compareAndSet(false, true))
            {
                throw new RuntimeException("Failed to lock " + this.name + ". It's already locked locally.");
            }
            logger.log(Level.FINE, "[Lock] Successfully acquired lock {0}", this.name);
            return true;
        }
        return false;
    }

    private synchronized boolean tryLock0()
    {
        final RedisCommands<String, byte[]> redis = this.observationManager.getRedis();
        return ((long) redis.eval("if (redis.call('exists', KEYS[1]) == 1) then\n" +
                               "return 0\n" +
                               "else\n" +
                               "redis.call('setex', KEYS[1], 30, 1)\n" +
                               "return 1\n" +
                               "end\n", ScriptOutputType.INTEGER, this.name)) == 1;
    }

    @Override
    public synchronized void unlock()
    {
        final Logger logger = this.observationManager.getMyLogger();
        if (! this.isLockedLocally.compareAndSet(true, false))
        {
            throw new IllegalStateException("Lock " + this.name + " isn't already locked locally! Lock may be expired or you didn't call Lock#lock()");
        }
        if (! this.tryUnlock())
        {
            throw new RuntimeException("Failed to unlock " + this.name);
        }
        logger.log(Level.FINE, "[Lock] Successfully unlocked {0}", this.name);
    }

    private boolean tryUnlock()
    {
        final RedisCommands<String, byte[]> redis = this.observationManager.getRedis();
        return ((long) redis.eval("if (redis.call('del', KEYS[1]) == 1) then\n" +
                                             "redis.call('publish', \"unlock\", KEYS[1])\n" +
                                             "return 1\n" +
                                             "else\n" +
                                             "return 0\n" +
                                             "end", ScriptOutputType.INTEGER, this.name)) == 1;

    }

    /*default*/ void remoteUnlock()
    {
        final Logger logger = this.observationManager.getMyLogger();
        if (this.isLockedLocally.compareAndSet(true, false))
        {
            logger.log(Level.WARNING, "[Lock] Lock {0} has been unlocked while it's locked locally. It will cause further issues.", this.name);
        }
        logger.log(Level.FINE, "[Lock] Remote unlock {0}", this.name);
        synchronized (this.waiter)
        {
            this.waiter.notifyAll();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("isLockedLocally", this.isLockedLocally).toString();
    }
}