package pl.north93.zgame.api.global.network.impl;

import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.daemon.DaemonDto;
import pl.north93.zgame.api.global.network.daemon.IDaemonsManager;
import pl.north93.zgame.api.global.redis.observable.Hash;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;

class DaemonsManagerImpl implements IDaemonsManager
{
    private final Hash<DaemonDto> daemons;

    public DaemonsManagerImpl(final IObservationManager observationManager)
    {
        this.daemons = observationManager.getHash(DaemonDto.class, "daemons");
    }

    @Override
    public Set<DaemonDto> all()
    {
        return this.daemons.values();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
