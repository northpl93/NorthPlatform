package pl.north93.northplatform.features.global;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.async.RedisAsyncCommands;
import lombok.SneakyThrows;
import pl.north93.northplatform.api.global.storage.StorageConnector;
import pl.north93.northplatform.api.global.storage.StringByteRedisCodec;

public class Benchmark2
{

    //@Bean
    public Benchmark2(final StorageConnector storageConnector)
    {
        final var connection1 = storageConnector.getRedisClient().connect(StringByteRedisCodec.INSTANCE).async();
        final var connection2 = storageConnector.getRedisClient().connect(StringByteRedisCodec.INSTANCE).async();

        for (int i = 0; i < 1; i++)
        {
            final var connection = i % 2 == 0 ? connection1 : connection2;
            new WorkerThread2(connection).start();
        }
    }
}

class WorkerThread2 extends Thread
{
    private static final String lockScriptSha = "ef85433459bf0eb58021158718a3541a2df202b6";
    private static final String unlockScriptSha = "24f2694068f1c03fbf54e4eb15e5e328dd46ce34";
    private final RedisAsyncCommands<String, byte[]> connection;

    public WorkerThread2(final RedisAsyncCommands<String, byte[]> connection)
    {
        this.connection = connection;
        this.setPriority(Thread.MAX_PRIORITY);
        this.setDaemon(true);
    }

    @Override
    @SneakyThrows
    public void run()
    {
        long second = System.currentTimeMillis();
        long operations = 0;

        final String valueName = RandomStringUtils.random(8);
        final String lockName = valueName + "_lock";
        final String publishName = "caval_upd:key:" + valueName;
        final byte[] threadId = RandomUtils.nextBytes(1);

        //final LockScripts lockScripts = new LockScripts(this.connection);
        for (int i = 0; i < 1_000_000; i++)
        {
            this.connection.evalsha(lockScriptSha, ScriptOutputType.BOOLEAN, new String[]{lockName}, threadId);
            //lockScripts.lock(lockName, threadId);

            // update value and publish it in redis, like a Value#update
            this.connection.set(valueName, threadId);
            this.connection.publish(publishName, threadId);

            this.connection.evalsha(unlockScriptSha, ScriptOutputType.INTEGER, new String[]{lockName}, threadId).get();
            //lockScripts.unlock(lockName, threadId);

            operations++;

            if (System.currentTimeMillis() - second >= 1000)
            {
                second = System.currentTimeMillis();
                System.out.println("UPDATES PER SECOND: " + operations);
                operations = 0;
            }
        }
    }
}
