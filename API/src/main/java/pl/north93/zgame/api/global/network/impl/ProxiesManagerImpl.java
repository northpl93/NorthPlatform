package pl.north93.zgame.api.global.network.impl;

import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.proxy.IProxiesManager;
import pl.north93.zgame.api.global.network.proxy.IProxyRpc;
import pl.north93.zgame.api.global.network.proxy.ProxyDto;
import pl.north93.zgame.api.global.network.server.ServerProxyData;
import pl.north93.zgame.api.global.redis.observable.Hash;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.Targets;

class ProxiesManagerImpl implements IProxiesManager
{
    private final IRpcManager rpcManager;
    private final Unsafe unsafe = new ProxiesManagerUnsafe();
    private final Hash<ProxyDto> proxies;

    public ProxiesManagerImpl(final IRpcManager rpcManager, final IObservationManager observationManager)
    {
        this.rpcManager = rpcManager;
        this.proxies = observationManager.getHash(ProxyDto.class, "proxies");
    }

    @Override
    public int onlinePlayersCount()
    {
        return this.all().stream().mapToInt(ProxyDto::getOnlinePlayers).sum();
    }

    @Override
    public Set<ProxyDto> all()
    {
        return this.proxies.values();
    }

    @Override
    public IProxyRpc getRpc(final ProxyDto proxyDto)
    {
        return this.rpcManager.createRpcProxy(IProxyRpc.class, Targets.proxy(proxyDto.getId()));
    }

    @Override
    public void addServer(final ServerProxyData proxyData)
    {
        for (final ProxyDto proxy : this.all())
        {
            final IProxyRpc rpc = this.getRpc(proxy);
            rpc.addServer(proxyData);
        }
    }

    @Override
    public void removeServer(final ServerProxyData proxyData)
    {
        for (final ProxyDto proxy : this.all())
        {
            final IProxyRpc rpc = this.getRpc(proxy);
            rpc.removeServer(proxyData);
        }
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
        public Hash<ProxyDto> getHash()
        {
            return ProxiesManagerImpl.this.proxies;
        }
    }
}
