package pl.north93.zgame.api.global.network.impl;

import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.daemon.DaemonDto;
import pl.north93.zgame.api.global.network.daemon.DaemonRpc;
import pl.north93.zgame.api.global.network.daemon.IDaemonsManager;
import pl.north93.zgame.api.global.redis.observable.Hash;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.RpcCustomTarget;

class DaemonsManagerImpl implements IDaemonsManager
{
    private final DaemonsManagerUnsafe unsafe = new DaemonsManagerUnsafe();
    private final IRpcManager rpcManager;
    private final Hash<DaemonDto> daemons;

    public DaemonsManagerImpl(final IRpcManager rpcManager, final IObservationManager observationManager)
    {
        this.rpcManager = rpcManager;
        this.daemons = observationManager.getHash(DaemonDto.class, "daemons");
    }

    @Override
    public Set<DaemonDto> all()
    {
        return this.daemons.values();
    }

    @Override
    public DaemonRpc getRpc(final String daemonId)
    {
        return this.rpcManager.createRpcProxy(DaemonRpc.class, new RpcCustomTarget(daemonId));
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

    private class DaemonsManagerUnsafe implements Unsafe
    {
        @Override
        public Hash<DaemonDto> getHash()
        {
            return DaemonsManagerImpl.this.daemons;
        }
    }
}
