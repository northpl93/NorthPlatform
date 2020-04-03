package pl.north93.northplatform.api.global.redis.observable.impl;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.lettuce.core.api.sync.RedisCommands;
import lombok.ToString;
import pl.north93.serializer.platform.NorthSerializer;

@ToString(of = {"lockScripts", "waitingLocks"})
class LockManagement
{
    private final NorthSerializer<byte[], byte[]> serializer;
    private final LockScripts lockScripts;
    private final Queue<LockImpl> waitingLocks;

    LockManagement(final NorthSerializer<byte[], byte[]> serializer, final RedisCommands<String, byte[]> redis)
    {
        this.serializer = serializer;
        this.lockScripts = new LockScripts(redis);
        this.waitingLocks = new ConcurrentLinkedQueue<>();
    }

    boolean lock(final String name, final long threadId)
    {
        final byte[] arg = this.serializer.serialize(Long.class, threadId);
        return this.lockScripts.lock(name, arg);
    }

    int unlock(final String name, final long threadId)
    {
        final byte[] arg = this.serializer.serialize(Long.class, threadId);
        return this.lockScripts.unlock(name, arg);
    }

    void addWaitingLock(final LockImpl lock)
    {
        this.waitingLocks.add(lock);
    }

    void removeWaitingLock(final LockImpl lock)
    {
        this.waitingLocks.remove(lock);
    }

    void unlockNotify(final String channel, final byte[] message)
    {
        final String lock = new String(message, StandardCharsets.UTF_8);

        final Iterator<LockImpl> lockIter = this.waitingLocks.iterator();
        while (lockIter.hasNext())
        {
            final LockImpl waitingLock = lockIter.next();
            if (waitingLock.getName().equals(lock))
            {
                lockIter.remove();
                waitingLock.remoteUnlock();
            }
        }
    }
}
