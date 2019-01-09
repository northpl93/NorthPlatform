package pl.north93.northplatform.api.global.redis.observable.impl;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.redis.observable.Lock;
import pl.north93.northplatform.api.global.redis.observable.Value;

abstract class CachedValue<T> implements Value<T>
{
    protected final ObservationManagerImpl observationManager;

    public CachedValue(final ObservationManagerImpl observationManager)
    {
        this.observationManager = observationManager;
    }

    @Override
    public T getOr(final Supplier<T> defaultValue)
    {
        final T optimisticResult = this.get();
        if (optimisticResult != null)
        {
            return optimisticResult;
        }

        // optymistyczna sciezka nie udala sie
        try (final Lock lock = this.lock())
        {
            final T secondTry = this.get();
            if (secondTry != null)
            {
                return secondTry;
            }

            final T newValue = defaultValue.get();
            this.set(newValue);
            return newValue;
        }
    }

    @Override
    public boolean update(final Function<T, T> update)
    {
        try (final Lock lock = this.lock())
        {
            final T t = this.get();
            if (t != null)
            {
                this.set(update.apply(t));
                return true;
            }

            return false;
        }
    }

    @Override
    public void ifPresent(final Consumer<T> action)
    {
        final T value = this.get();
        if (value != null)
        {
            action.accept(value);
        }
    }

    @Override
    public final Lock lock()
    {
        return this.getLock().lock();
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
