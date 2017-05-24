package pl.north93.zgame.api.global.redis.observable.impl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.observable.Value;

abstract class CachedValue<T> implements Value<T>
{
    protected final ObservationManagerImpl observationManager;

    public CachedValue(final ObservationManagerImpl observationManager)
    {
        this.observationManager = observationManager;
    }

    // zwraca unikalna nazwe tej wartosci, sluzy do nasluchiwania
    abstract String getInternalName();

    // obsluguje nowa wartosc przychodzaca z sieci
    abstract void handleNewValue(final byte[] newValue);

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
