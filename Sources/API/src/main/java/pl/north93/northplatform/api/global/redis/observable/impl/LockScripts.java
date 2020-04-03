package pl.north93.northplatform.api.global.redis.observable.impl;

import java.nio.charset.StandardCharsets;

import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.ToString;

@ToString(of = {"lockScriptSha", "unlockScriptSha"})
final class LockScripts
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

    private final RedisCommands<String, byte[]> redis;
    private final String lockScriptSha;
    private final String unlockScriptSha;

    public LockScripts(final RedisCommands<String, byte[]> redis)
    {
        this.redis = redis;

        final byte[] lockScriptBytes = LOCK_SCRIPT.getBytes(StandardCharsets.UTF_8);
        this.lockScriptSha = redis.scriptLoad(lockScriptBytes);

        final byte[] unlockScriptBytes = UNLOCK_SCRIPT.getBytes(StandardCharsets.UTF_8);
        this.unlockScriptSha = redis.scriptLoad(unlockScriptBytes);
    }

    public boolean lock(final String name, final byte[] threadId)
    {
        return this.redis.evalsha(this.lockScriptSha, ScriptOutputType.BOOLEAN, new String[]{name}, threadId);
    }

    public int unlock(final String name, final byte[] threadId)
    {
        final Long eval = this.redis.evalsha(this.unlockScriptSha, ScriptOutputType.INTEGER, new String[]{name}, threadId);
        return eval.intValue();
    }
}
