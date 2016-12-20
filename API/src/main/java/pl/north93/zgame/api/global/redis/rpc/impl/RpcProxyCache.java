package pl.north93.zgame.api.global.redis.rpc.impl;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.rpc.IRpcTarget;

class RpcProxyCache
{
    private final RpcManagerImpl          rpcManager;
    private final Map<CacheEntry, Object> cache = new HashMap<>();

    public RpcProxyCache(final RpcManagerImpl rpcManager)
    {
        this.rpcManager = rpcManager;
    }

    public Object get(final Class<?> classInterface, final IRpcTarget target)
    {
        final CacheEntry cacheEntry = new CacheEntry(classInterface, target);
        final Object cacheProxy = this.cache.get(cacheEntry);
        if (cacheProxy == null)
        {
            final Object proxy = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] {classInterface}, new RpcInvocationHandler(this.rpcManager, classInterface, target));
            this.cache.put(cacheEntry, proxy);
            return proxy;
        }
        return cacheProxy;
    }

    private static final class CacheEntry
    {
        private final Class<?>   interfaceClass;
        private final IRpcTarget target;

        public CacheEntry(final Class<?> interfaceClass, final IRpcTarget target)
        {
            this.interfaceClass = interfaceClass;
            this.target = target;
        }

        @Override
        public boolean equals(final Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || this.getClass() != o.getClass())
            {
                return false;
            }

            final CacheEntry that = (CacheEntry) o;
            return this.interfaceClass.equals(that.interfaceClass) && this.target.equals(that.target);

        }

        @Override
        public int hashCode()
        {
            int result = this.interfaceClass.hashCode();
            result = 31 * result + this.target.hashCode();
            return result;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("interfaceClass", this.interfaceClass).append("target", this.target).toString();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("cache", this.cache).toString();
    }
}
