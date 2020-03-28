package pl.north93.northplatform.api.global.redis.rpc.impl;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.north93.northplatform.api.global.redis.rpc.IRpcTarget;

@ToString(of = "cache")
class RpcProxyCache
{
    private final RpcManagerImpl rpcManager;
    private final Map<CacheEntry, Object> cache = new ConcurrentHashMap<>();

    public RpcProxyCache(final RpcManagerImpl rpcManager)
    {
        this.rpcManager = rpcManager;
    }

    public Object get(final Class<?> classInterface, final IRpcTarget target)
    {
        final CacheEntry cacheEntry = new CacheEntry(classInterface, target);
        return this.cache.computeIfAbsent(cacheEntry, entry ->
        {
            final Class<?>[] classes = {classInterface};
            final RpcInvocationHandler invocationHandler = new RpcInvocationHandler(this.rpcManager, classInterface, target);

            return Proxy.newProxyInstance(classInterface.getClassLoader(), classes, invocationHandler);
        });
    }

    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor
    private static final class CacheEntry
    {
        private final Class<?>   interfaceClass;
        private final IRpcTarget target;
    }
}
