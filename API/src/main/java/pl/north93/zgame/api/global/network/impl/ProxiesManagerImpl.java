package pl.north93.zgame.api.global.network.impl;

import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.proxy.IProxiesManager;
import pl.north93.zgame.api.global.network.proxy.ProxyInstanceInfo;
import pl.north93.zgame.api.global.redis.observable.Hash;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;

public class ProxiesManagerImpl implements IProxiesManager
{
    private final Unsafe unsafe = new ProxiesManagerUnsafe();
    private final Hash<ProxyInstanceInfo> proxies;

    public ProxiesManagerImpl(final IObservationManager observationManager)
    {
        this.proxies = observationManager.getHash(ProxyInstanceInfo.class, "proxies");
    }

    @Override
    public int onlinePlayersCount()
    {
        return this.getProxyServers().stream().mapToInt(ProxyInstanceInfo::getOnlinePlayers).sum();
    }

    @Override
    public Set<ProxyInstanceInfo> getProxyServers()
    {
        return this.proxies.values();
    }

    @Override
    public Unsafe unsafe()
    {
        return this.unsafe;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }

    private class ProxiesManagerUnsafe implements Unsafe
    {
        @Override
        public Hash<ProxyInstanceInfo> getHash()
        {
            return ProxiesManagerImpl.this.proxies;
        }
    }
}
