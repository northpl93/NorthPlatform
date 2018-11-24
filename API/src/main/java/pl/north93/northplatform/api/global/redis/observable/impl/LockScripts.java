package pl.north93.northplatform.api.global.redis.observable.impl;

import com.lambdaworks.redis.ScriptOutputType;
import com.lambdaworks.redis.api.sync.RedisCommands;

import pl.north93.northplatform.api.global.serializer.platform.NorthSerializer;

/*default*/ final class LockScripts
{
    private static final String LOCK_SCRIPT =
                    "if(redis.call('exists',KEYS[1])==1) then\n" +
                        "if(redis.call('hexists',KEYS[1],ARGV[1])==1) then\n" +
                            "redis.call('hincrby',KEYS[1],ARGV[1],1)\n" +
                            "return true\n" +
                        "else\n" +
                            "return false\n" +
                        "end\n" +
                    "else\n" +
                        "redis.call('hset',KEYS[1],ARGV[1],1)\n" +
                        "redis.call('expire',KEYS[1],30)\n" +
                        "return true\n" +
                    "end";

    private static final String UNLOCK_SCRIPT =
                    "if(redis.call('exists',KEYS[1])==1) then\n" +
                        "local result = redis.call('hget',KEYS[1],ARGV[1])\n" + // z jakiegos powodu to jest stringiem...
                        "if(result=='0') then\n" +
                            "return 2\n" +
                        "elseif(result=='1') then\n" +
                            "redis.call('del',KEYS[1])\n" +
                            "redis.call('publish',\"unlock\",KEYS[1])\n" + // wysylamy info o unlocku tylko gdy calkowicie wychodzimy z locka
                            "return 1\n" +
                        "else\n" +
                            "redis.call('hincrby',KEYS[1],ARGV[1],-1)\n" +
                            "return 1\n" +
                        "end\n" +
                    "else\n" +
                        "return 0\n" +
                    "end";

    public static boolean lock(final ObservationManagerImpl observationManager, final String name, final long threadId)
    {
        final RedisCommands<String, byte[]> redis = observationManager.getRedis();
        final NorthSerializer<byte[]> msgPack = observationManager.getMsgPack();

        final byte[] arg = msgPack.serialize(Long.class, threadId);
        return redis.eval(LockScripts.LOCK_SCRIPT, ScriptOutputType.BOOLEAN, new String[]{name}, arg);
    }

    public static int unlock(final ObservationManagerImpl observationManager, final String name, final long threadId)
    {
        final RedisCommands<String, byte[]> redis = observationManager.getRedis();
        final NorthSerializer<byte[]> msgPack = observationManager.getMsgPack();

        final byte[] arg = msgPack.serialize(Long.class, threadId);
        final Long eval = redis.eval(LockScripts.UNLOCK_SCRIPT, ScriptOutputType.INTEGER, new String[]{name}, arg);

        return eval.intValue();
    }
}
