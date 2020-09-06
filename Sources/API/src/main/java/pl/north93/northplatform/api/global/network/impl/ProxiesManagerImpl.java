package pl.north93.northplatform.api.global.network.impl;

import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.network.proxy.IProxiesManager;
import pl.north93.northplatform.api.global.network.proxy.IProxyRpc;
import pl.north93.northplatform.api.global.network.proxy.ProxyDto;
import pl.north93.northplatform.api.global.network.server.Server;
import pl.north93.northplatform.api.global.redis.observable.Hash;
import pl.north93.northplatform.api.global.redis.observable.IObservationManager;
import pl.north93.northplatform.api.global.redis.rpc.IRpcManager;
import pl.north93.northplatform.api.global.redis.rpc.Targets;

class ProxiesManagerImpl implements IProxiesManager
{
    private final IRpcManager rpcManager;
    private final Hash<ProxyDto> proxies;

    @Bean
    private ProxiesManagerImpl(final IRpcManager rpcManager, final IObservationManager observationManager)
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
    public void addOrUpdateProxy(final String proxyId, final ProxyDto proxyDto)
    {
        this.proxies.put(proxyId, proxyDto);
    }

    @Override
    public void removeProxy(final String proxyId)
    {
        this.proxies.delete(proxyId);
    }

    @Override
    public void addServer(final Server server)
    {
        for (final ProxyDto proxy : this.all())
        {
            final IProxyRpc rpc = this.getRpc(proxy);
            rpc.addServer(server);
        }
    }

    @Override
    public void removeServer(final Server server)
    {
        for (final ProxyDto proxy : this.all())
        {
            final IProxyRpc rpc = this.getRpc(proxy);
            rpc.removeServer(server);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
