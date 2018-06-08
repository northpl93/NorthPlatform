package pl.north93.zgame.api.global.redis.observable.impl;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;
import com.lambdaworks.redis.ScriptOutputType;
import com.lambdaworks.redis.api.sync.RedisCommands;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.observable.Lock;

class LockImpl implements Lock
{
    private static final String LOCK_SCRIPT = "if(redis.call('exists',KEYS[1])==1) then\nreturn false\nelse\nredis.call('setex',KEYS[1],30,1)\nreturn true\nend";
    private static final String UNLOCK_SCRIPT = "if(redis.call('del',KEYS[1])==1) then\nredis.call('publish',\"unlock\",KEYS[1])\nreturn true\nelse\nreturn false\nend";
    private final ObservationManagerImpl observationManager;
    private final String                 name;
    private final Object                 waiter;
    private       boolean                isLockedLocally;

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
    public LockImpl lock()
    {
        final Logger logger = this.observationManager.getMyLogger();
        if (this.tryLock0())
        {
            logger.log(Level.FINE, "[Lock] Successfully acquired lock {0}", this.name);
        }
        else
        {
            this.observationManager.addWaitingLock(this);
            logger.log(Level.FINE, "[Lock] Lock {0} is waiting...", this.name);
            while (! this.tryLock0())
            {
                this.awaitUnlockOrTimeout();
            }
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
            logger.log(Level.FINE, "[Lock] Successfully acquired lock {0}", this.name);
            return true;
        }
        return false;
    }

    private synchronized boolean tryLock0()
    {
        if (this.isLockedLocally)
        {
            return false;
        }

        final RedisCommands<String, byte[]> redis = this.observationManager.getRedis();
        return this.isLockedLocally = redis.eval(LOCK_SCRIPT, ScriptOutputType.BOOLEAN, this.name);
    }

    @Override
    public void unlock()
    {
        final Logger logger = this.observationManager.getMyLogger();
        if (! this.tryUnlock())
        {
            throw new RuntimeException("Failed to unlock " + this.name);
        }
        logger.log(Level.FINE, "[Lock] Successfully unlocked {0}", this.name);
    }

    private synchronized boolean tryUnlock()
    {
        Preconditions.checkState(this.isLockedLocally, "Lock isn't locked locally");

        final RedisCommands<String, byte[]> redis = this.observationManager.getRedis();
        if (redis.eval(UNLOCK_SCRIPT, ScriptOutputType.BOOLEAN, this.name))
        {
            this.observationManager.removeWaitingLock(this);
            this.isLockedLocally = false;

            return true;
        }

        return false;
    }

    /*default*/ synchronized void remoteUnlock()
    {
        final Logger logger = this.observationManager.getMyLogger();
        if (this.isLockedLocally)
        {
            logger.log(Level.SEVERE, "[Lock] Lock {0} has been unlocked while it's locked locally. It will cause further issues.", this.name);
        }

        synchronized (this.waiter)
        {
            logger.log(Level.FINE, "[Lock] Remote unlock {0}", this.name);

            this.isLockedLocally = false;
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
            e.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("isLockedLocally", this.isLockedLocally).toString();
    }
}